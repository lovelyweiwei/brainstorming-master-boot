package com.weiwei.brainstorming.judge.codesandbox.impl;

import com.weiwei.brainstorming.judge.codesandbox.CodeSandBox;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeRequest;
import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeResponse;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.enums.JudgeInfoMessageEnum;
import com.weiwei.brainstorming.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 实例代码沙箱（实例-跑通代码流程）
 *
 * @Author weiwei
 * @Date 2023/9/4 18:58
 * @Version 1.0
 */
@Slf4j
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        //executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        //executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
