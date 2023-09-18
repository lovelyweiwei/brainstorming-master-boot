package com.weiwei.brainstorming.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiwei.brainstorming.common.ErrorCode;
import com.weiwei.brainstorming.constant.CommonConstant;
import com.weiwei.brainstorming.exception.BusinessException;
import com.weiwei.brainstorming.judge.JudgeService;
import com.weiwei.brainstorming.judge.codesandbox.CodeSandBox;
import com.weiwei.brainstorming.judge.codesandbox.CodeSandBoxFactory;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeRequest;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeResponse;
import com.weiwei.brainstorming.manager.RedisLimiterManager;
import com.weiwei.brainstorming.mapper.QuestionMapper;
import com.weiwei.brainstorming.mapper.QuestionSubmitMapper;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionRunRequest;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.entity.User;
import com.weiwei.brainstorming.model.enums.ExecuteCodeStatusEnum;
import com.weiwei.brainstorming.model.enums.QuestionDifficultyEnum;
import com.weiwei.brainstorming.model.enums.QuestionSubmitLanguageEnum;
import com.weiwei.brainstorming.model.enums.QuestionSubmitStatusEnum;
import com.weiwei.brainstorming.model.vo.QuestionRunResult;
import com.weiwei.brainstorming.model.vo.QuestionSubmitVO;
import com.weiwei.brainstorming.model.vo.SubmitSummaryVO;
import com.weiwei.brainstorming.model.vo.UserVO;
import com.weiwei.brainstorming.rabbitmq.CodeMqProducer;
import com.weiwei.brainstorming.service.QuestionService;
import com.weiwei.brainstorming.service.QuestionSubmitService;
import com.weiwei.brainstorming.service.UserService;
import com.weiwei.brainstorming.utils.SqlUtils;
import io.lettuce.core.RedisClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-08-31 18:27:23
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    @Lazy
    private QuestionService questionService;
    @Resource
    private UserService userService;

    @Resource
    @Lazy // 懒加载，解决循环依赖
    private JudgeService judgeService;

    @Value("${codesandbox.type:remote}")
    private String type;

    @Resource
    private CodeMqProducer myMessageProducer;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 检验语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求参数不存在");
        }

        //将problem的提交数+1
        questionMapper.update(null, new UpdateWrapper<Question>()
                .setSql("submitNum = submitNum + 1").eq("id", question.getId()));

        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean result = this.save(questionSubmit);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        //限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("doQuestionSubmit_" + loginUser.getId());

        // 执行判题服务
        Long questionSubmitId = questionSubmit.getId();

        // 使用消息队列
        myMessageProducer.sendMessage("oj_exchange", "oj", String.valueOf(questionSubmitId));
        //judgeService.doJudge(questionSubmitId);

        // 异步
        //CompletableFuture.runAsync(() -> {
        //    judgeService.doJudge(questionSubmitId);
        //});
        return questionSubmitId;
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();

        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        String sortField = questionSubmitQueryRequest.getSortField();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        // 处理脱敏
        if (loginUser.getId() != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        Long userId = questionSubmitVO.getUserId();
        User byId = userService.getById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(byId, userVO);
        questionSubmitVO.setUserVO(userVO);
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> {
                    return getQuestionSubmitVO(questionSubmit, loginUser);
                })
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;

        //性能高的写法
        // 1. 关联查询用户信息
        //Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        //Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
        //        .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        //List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
        //    QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //    Long userId = questionSubmit.getUserId();
        //    User user = null;
        //    if (userIdUserListMap.containsKey(userId)) {
        //        user = userIdUserListMap.get(userId).get(0);
        //    }
        //    questionSubmitVO.setUserVO(userService.getUserVO(user));
        //    return questionSubmitVO;
        //}).collect(Collectors.toList());


    }

    public SubmitSummaryVO getSubmitSummary(HttpServletRequest request) {
        SubmitSummaryVO summaryVo = new SubmitSummaryVO();

        User currentUser = userService.getLoginUser(request);

        //获取简单、中等、困难题目ids
        List<Long> easyIds = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, QuestionDifficultyEnum.EASY.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        List<Long> mediumIds = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, QuestionDifficultyEnum.MID.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        List<Long> hardIds = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, QuestionDifficultyEnum.HARD.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        int easyTotal = easyIds.size();
        int mediumTotal = mediumIds.size();
        int hardTotal = hardIds.size();
        summaryVo.setEasyTotal(easyTotal);
        summaryVo.setMediumTotal(mediumTotal);
        summaryVo.setHardTotal(hardTotal);
        summaryVo.setTotal(easyTotal + mediumTotal + hardTotal);

        //获取用户通过的简单、中等、困难题目数
        Integer easyPass = baseMapper.getPassCount(currentUser.getId(), easyIds);
        Integer mediumPass = baseMapper.getPassCount(currentUser.getId(), mediumIds);
        Integer hardPass = baseMapper.getPassCount(currentUser.getId(), hardIds);
        summaryVo.setEasyPass(easyPass);
        summaryVo.setMediumPass(mediumPass);
        summaryVo.setHardPass(hardPass);

        //获取用户提交总数
        Integer submitCount = Math.toIntExact(baseMapper.selectCount(new LambdaQueryWrapper<QuestionSubmit>()
                .eq(QuestionSubmit::getUserId, currentUser.getId())));
        summaryVo.setSubmitCount(submitCount);
        //获取用户成功的提交
        Integer passCount = Math.toIntExact(baseMapper.selectCount(new LambdaQueryWrapper<QuestionSubmit>()
                .eq(QuestionSubmit::getUserId, currentUser.getId())
                .eq(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue())));
        summaryVo.setPassCount(passCount);

        return summaryVo;
    }

    @Override
    public QuestionRunResult doQuestionRun(QuestionRunRequest questionRunRequest) {
        String code = questionRunRequest.getCode();
        String language = questionRunRequest.getLanguage();
        List<String> inputList = Collections.singletonList(questionRunRequest.getInput());

        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse response = codeSandBox.executeCode(executeCodeRequest);

        return getQuestionRunVo(questionRunRequest.getInput(), response);
    }

    private static QuestionRunResult getQuestionRunVo(String input, ExecuteCodeResponse response) {
        QuestionRunResult questionRunResult = new QuestionRunResult();
        questionRunResult.setInput(input);
        //执行成功
        if (response.getStatus().equals(ExecuteCodeStatusEnum.SUCCESS.getValue())) {
            questionRunResult.setCode(ExecuteCodeStatusEnum.SUCCESS.getValue());
            questionRunResult.setOutput(response.getResults().get(0).getMessage());
        } else if (response.getStatus().equals(ExecuteCodeStatusEnum.RUN_FAILED.getValue())) {
            questionRunResult.setCode(ExecuteCodeStatusEnum.RUN_FAILED.getValue());
            questionRunResult.setOutput(response.getMessage());
        } else if (response.getStatus().equals(ExecuteCodeStatusEnum.COMPILE_FAILED.getValue())) {
            questionRunResult.setCode(ExecuteCodeStatusEnum.COMPILE_FAILED.getValue());
            questionRunResult.setOutput(response.getMessage());
        }
        return questionRunResult;
    }
}




