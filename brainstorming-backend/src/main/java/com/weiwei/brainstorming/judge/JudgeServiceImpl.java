package com.weiwei.brainstorming.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.weiwei.brainstorming.common.ErrorCode;
import com.weiwei.brainstorming.exception.BusinessException;
import com.weiwei.brainstorming.judge.codesandbox.CodeSandBox;
import com.weiwei.brainstorming.judge.codesandbox.CodeSandBoxFactory;
import com.weiwei.brainstorming.judge.codesandbox.CodeSandBoxProxy;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeRequest;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeResponse;
import com.weiwei.brainstorming.judge.strategy.JudgeContext;
import com.weiwei.brainstorming.model.dto.question.JudgeCase;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.enums.JudgeInfoMessageEnum;
import com.weiwei.brainstorming.model.enums.QuestionSubmitStatusEnum;
import com.weiwei.brainstorming.service.QuestionService;
import com.weiwei.brainstorming.service.QuestionSubmitService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务实现类
 *
 * @Author weiwei
 * @Date 2023/9/4 19:48
 * @Version 1.0
 */
@Service
public class JudgeServiceImpl implements JudgeService {


    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1、传入题目的提交id，获取对应的题目、提交信息（代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (ObjectUtils.isEmpty(questionSubmit)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (ObjectUtils.isEmpty(question)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2、更改判题状态( 判题中），如果判题状态不为等待中，则不执行
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }

        // 3、调用沙箱，获取执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String judgeCaseStr = question.getJudgeCase();
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);

        // 4、根据沙箱的执行结果，设置题目的判题状态和信息 --- 使用策略模式
        JudgeContext judgeContext = new JudgeContext();
        //judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setExecuteCodeResponse(executeCodeResponse);
        judgeContext.setInputList(inputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        //JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        // 太冗余，要是有很多策略，那么就有很多 if else  -> judgeManager 简化调用
        //if ("java".equals(language)) {
        //    judgeStrategy = new JavaLanguageJudgeStrategy();
        //}
        //JudgeInfo judgeInfo = judgeStrategy.doJudge(judgeContext);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);  //根据沙箱返回结果进行判断处理



        // 5、修改数据库的判题结果
        boolean judgeResult = judgeInfo.getStatus().equals(JudgeInfoMessageEnum.ACCEPTED.getValue());

        questionSubmitUpdate.setStatus(judgeResult ?
                QuestionSubmitStatusEnum.SUCCEED.getValue() :
                QuestionSubmitStatusEnum.FAILED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }

        // 6、修改题目的通过数
        if (judgeResult) {
            //将question的通过数+1
            questionService.update(null, new UpdateWrapper<Question>()
                    .setSql("acceptedNum = acceptedNum + 1").eq("id", question.getId()));
        }

        return questionSubmitUpdate;
    }
}
