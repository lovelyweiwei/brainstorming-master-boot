package com.weiwei.brainstorming.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.weiwei.brainstorming.common.ErrorCode;
import com.weiwei.brainstorming.constant.CommonConstant;
import com.weiwei.brainstorming.exception.BusinessException;
import com.weiwei.brainstorming.exception.ThrowUtils;
import com.weiwei.brainstorming.mapper.QuestionMapper;
import com.weiwei.brainstorming.mapper.QuestionSubmitMapper;
import com.weiwei.brainstorming.model.dto.question.JudgeConfig;
import com.weiwei.brainstorming.model.dto.question.QuestionQueryRequest;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.entity.User;
import com.weiwei.brainstorming.model.enums.QuestionSubmitStatusEnum;
import com.weiwei.brainstorming.model.vo.QuestionVO;
import com.weiwei.brainstorming.model.vo.SafeQuestionVO;
import com.weiwei.brainstorming.model.vo.UserVO;
import com.weiwei.brainstorming.service.QuestionService;
import com.weiwei.brainstorming.service.QuestionSubmitService;
import com.weiwei.brainstorming.service.UserService;
import com.weiwei.brainstorming.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-08-31 18:26:25
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    private final static Gson GSON = new Gson();

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    private UserService userService;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String difficulty = question.getDifficulty();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags, difficulty), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String difficulty = questionQueryRequest.getDifficulty();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        String status = questionQueryRequest.getStatus();

        //不查询content和answer，因为很多时候不显示
        queryWrapper.select(Question.class, item -> !item.getColumn().equals("content") && !item.getColumn().equals("answer"));

        if (StrUtil.isNotBlank(status) && !status.equals("全部")) {
            User currentUser = userService.getLoginUser(request);
            Set<Long> passedIds;
            Set<Long> triedIds;

            switch (status) {
                case "已通过":
                    passedIds = questionSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>()
                                    .select(QuestionSubmit::getQuestionId).eq(QuestionSubmit::getUserId, currentUser.getId())
                                    .eq(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue()))
                            .stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
                    if (passedIds.isEmpty()) {
                        return null;
                    }
                    queryWrapper.in("id", passedIds);
                    break;
                case "尝试过":
                    passedIds = questionSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>()
                                    .select(QuestionSubmit::getQuestionId).eq(QuestionSubmit::getUserId, currentUser.getId())
                                    .eq(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue()))
                            .stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
                    triedIds = questionSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>()
                                    .select(QuestionSubmit::getQuestionId).eq(QuestionSubmit::getUserId, currentUser.getId())
                                    .ne(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue()))
                            .stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
                    triedIds = (Set<Long>) CollUtil.subtract(triedIds, passedIds);
                    if (triedIds.isEmpty()) {
                        return null;
                    }
                    queryWrapper.in("id", triedIds);
                    break;
                case "未开始":
                    triedIds = questionSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>()
                                    .select(QuestionSubmit::getQuestionId).eq(QuestionSubmit::getUserId, currentUser.getId()))
                            .stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
                    if (!triedIds.isEmpty()) {
                        queryWrapper.notIn("id", triedIds);
                    }
                    break;
            }
        }


        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq(StringUtils.isNotBlank(difficulty), "difficulty", difficulty);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));

            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public List<String> getQuestionTags() {
        return lambdaQuery().select(Question::getTags).list().stream()
                .flatMap(question -> JSONUtil.toList(question.getTags(), String.class).stream())
                .distinct().collect(Collectors.toList());
    }

    @Override
    public SafeQuestionVO objToVo(Question question, Long uid) {
        if (question == null) {
            return null;
        }
        SafeQuestionVO safeQuestionVO = new SafeQuestionVO();
        BeanUtils.copyProperties(question, safeQuestionVO);
        safeQuestionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        safeQuestionVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));

        //查询当前用户历史做题信息（已通过、尝试过、未开始）
        QuestionSubmit submit = questionSubmitMapper.selectOne(new QueryWrapper<QuestionSubmit>()
                .select("max(status) as status").lambda()
                .eq(QuestionSubmit::getQuestionId, question.getId())
                .eq(QuestionSubmit::getUserId, uid));

        if(submit == null){
            safeQuestionVO.setStatus("未开始");
        } else if(submit.getStatus().equals(QuestionSubmitStatusEnum.SUCCEED.getValue())) {
            safeQuestionVO.setStatus("已通过");
        } else if(submit.getStatus().equals(QuestionSubmitStatusEnum.FAILED.getValue())){
            safeQuestionVO.setStatus("尝试过");
        } else {
            safeQuestionVO.setStatus("未开始");
        }

        return safeQuestionVO;
    }
}




