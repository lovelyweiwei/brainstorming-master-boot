package com.weiwei.brainstorming.judge;

import com.weiwei.brainstorming.judge.strategy.DefaultJudgeStrategy;
import com.weiwei.brainstorming.judge.strategy.JavaLanguageJudgeStrategy;
import com.weiwei.brainstorming.judge.strategy.JudgeContext;
import com.weiwei.brainstorming.judge.strategy.JudgeStrategy;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 *
 * @Author weiwei
 * @Date 2023/9/5 0:33
 * @Version 1.0
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        if ("java".equals(questionSubmit.getLanguage())) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
