package com.weiwei.brainstorming.judge.strategy;

import com.weiwei.brainstorming.judge.codesandbox.model.ExecuteCodeResponse;
import com.weiwei.brainstorming.model.dto.question.JudgeCase;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 *
 * @Author weiwei
 * @Date 2023/9/4 20:23
 * @Version 1.0
 */
@Data
public class JudgeContext {

    private List<String> inputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

    private ExecuteCodeResponse executeCodeResponse;
}
