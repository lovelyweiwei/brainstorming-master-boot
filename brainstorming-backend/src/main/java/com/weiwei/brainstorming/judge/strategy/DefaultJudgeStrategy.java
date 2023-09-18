package com.weiwei.brainstorming.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.weiwei.brainstorming.model.dto.question.JudgeCase;
import com.weiwei.brainstorming.model.dto.question.JudgeConfig;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * @Author weiwei
 * @Date 2023/9/4 20:25
 * @Version 1.0
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        ////从 上下文对象中取
        //JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        //List<String> inputList = judgeContext.getInputList();
        //List<String> outputList = judgeContext.getOutputList();
        //Question question = judgeContext.getQuestion();
        //List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        //Long memory = judgeInfo.getMemory();
        //Long time = judgeInfo.getTime();
        //
        //JudgeInfo judgeInfoResponse = new JudgeInfo();
        //judgeInfoResponse.setMemory(memory);
        //judgeInfoResponse.setTime(time);
        //
        //JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED; //默认
        //if (outputList.size() != inputList.size()) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    return judgeInfoResponse;
        //}
        //// 依次对比预期和实际的输出结果
        //for (int i = 0; i < judgeCaseList.size(); i++) {
        //    JudgeCase judgeCase = judgeCaseList.get(i);
        //    if (!judgeCase.getOutput().equals(outputList.get(i))) {
        //        judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
        //        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //        return judgeInfoResponse;
        //    }
        //}
        ////判断题目限制
        //String judgeConfigStr = question.getJudgeConfig();
        //JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        //Long needTimeLimit = judgeConfig.getTimeLimit();
        //Long needMemoryLimit = judgeConfig.getMemoryLimit();
        //if (memory > needMemoryLimit) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    return judgeInfoResponse;
        //}
        //if (time > needTimeLimit) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    return judgeInfoResponse;
        //}
        //judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //return judgeInfoResponse;
        return null;
    }
}
