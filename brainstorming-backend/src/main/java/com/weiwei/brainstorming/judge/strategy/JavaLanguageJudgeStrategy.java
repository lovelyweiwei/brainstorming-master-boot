package com.weiwei.brainstorming.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeResponse;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteMessage;
import com.weiwei.brainstorming.model.dto.question.JudgeCase;
import com.weiwei.brainstorming.model.dto.question.JudgeConfig;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.enums.ExecuteCodeStatusEnum;
import com.weiwei.brainstorming.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author weiwei
 * @Date 2023/9/4 20:25
 * @Version 1.0
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        //从 上下文对象中取
        List<String> inputList = judgeContext.getInputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        ExecuteCodeResponse executeCodeResponse = judgeContext.getExecuteCodeResponse();

        JudgeInfo judgeInfo = new JudgeInfo();
        //设置默认值
        judgeInfo.setStatus(ExecuteCodeStatusEnum.RUN_FAILED.getText());
        judgeInfo.setPass(0);
        int total = judgeCaseList.size();
        judgeInfo.setTotal(total);
        //执行成功
        if (executeCodeResponse.getStatus().equals(ExecuteCodeStatusEnum.SUCCESS.getValue())) {
            //期望输出
            List<String> expectedOutput = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
            //测试用例详细信息
            List<ExecuteMessage> results = executeCodeResponse.getResults();
            //实际输出
            List<String> output = results.stream().map(ExecuteMessage::getMessage).collect(Collectors.toList());
            //判题配置
            JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);

            //设置通过的测试用例
            int pass = 0;
            //设置最大实行时间
            long maxTime = Long.MIN_VALUE;
            long maxMemory = Long.MIN_VALUE;
            for (int i = 0; i < total; i++) {
                //判断执行时间
                Long time = results.get(i).getTime();
                if (time > maxTime) {
                    maxTime = time;
                }
                Long memory = Optional.ofNullable(results.get(i).getMemory()).orElse(0L);
                if (memory > maxMemory) {
                    maxMemory = memory;
                }
                if (expectedOutput.get(i).equals(output.get(i))) {
                    //超时
                    if (maxTime > judgeConfig.getTimeLimit()) {
                        judgeInfo.setTime(maxTime);
                        judgeInfo.setPass(pass);
                        judgeInfo.setStatus(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
                        judgeInfo.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getText());
                        break;
                    } else if (memory > judgeConfig.getMemoryLimit()) {
                        judgeInfo.setTime(maxTime);
                        judgeInfo.setPass(pass);
                        judgeInfo.setStatus(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
                        judgeInfo.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getText());
                        break;
                    } else {
                        pass++;
                    }
                } else {
                    //遇到了一个没通过的
                    judgeInfo.setPass(pass);
                    judgeInfo.setTime(maxTime);
                    judgeInfo.setStatus(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                    judgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getText());
                    //设置输出和预期输出信息
                    judgeInfo.setInput(inputList.get(i));
                    judgeInfo.setOutput(output.get(i));
                    judgeInfo.setExpectedOutput(expectedOutput.get(i));
                    break;
                }
            }
            if (pass == total) {
                judgeInfo.setPass(total);
                judgeInfo.setTime(maxTime);
                judgeInfo.setStatus(JudgeInfoMessageEnum.ACCEPTED.getValue());
                judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
            }
        } else if (executeCodeResponse.getStatus().equals(ExecuteCodeStatusEnum.RUN_FAILED.getValue())) {
            judgeInfo.setPass(0);
            judgeInfo.setStatus(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
            judgeInfo.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getText() + executeCodeResponse.getMessage());
        } else if (executeCodeResponse.getStatus().equals(ExecuteCodeStatusEnum.COMPILE_FAILED.getValue())) {
            judgeInfo.setPass(0);
            judgeInfo.setStatus(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            judgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText() + executeCodeResponse.getMessage());
        }

        return judgeInfo;
        //
        //JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED; //默认
        ////设置通过的测试用例
        //int pass = 0;
        //// 依次对比预期和实际的输出结果
        //for (int i = 0; i < judgeCaseList.size(); i++) {
        //    JudgeCase judgeCase = judgeCaseList.get(i);
        //    if (!judgeCase.getOutput().equals(outputList.get(i))) {
        //        judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
        //        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //        judgeInfoResponse.setPass(pass);
        //        return judgeInfoResponse;
        //    }
        //    //答案正确
        //    pass++;
        //}
        ////判断题目限制
        //String judgeConfigStr = question.getJudgeConfig();
        //JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        //Long needTimeLimit = judgeConfig.getTimeLimit();
        //Long needMemoryLimit = judgeConfig.getMemoryLimit();
        //if (memory > needMemoryLimit) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    judgeInfoResponse.setPass(pass - 1);//todo 需要具体修改
        //    return judgeInfoResponse;
        //}
        //// java 程序需要额外执行 10 秒钟
        //long JAVA_PROGRAM_TIME_COST = 10000L;
        //if ((time - JAVA_PROGRAM_TIME_COST) > needTimeLimit) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    judgeInfoResponse.setPass(pass - 1);//todo 需要具体修改
        //    return judgeInfoResponse;
        //}
        //if (outputList.size() != inputList.size()) {
        //    judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
        //    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //    return judgeInfoResponse;
        //}
        //judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        //return judgeInfoResponse;
    }
}
