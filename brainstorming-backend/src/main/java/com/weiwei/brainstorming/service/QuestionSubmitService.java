package com.weiwei.brainstorming.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionRunRequest;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.entity.User;
import com.weiwei.brainstorming.model.vo.QuestionRunResult;
import com.weiwei.brainstorming.model.vo.QuestionSubmitVO;
import com.weiwei.brainstorming.model.vo.SubmitSummaryVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2023-08-31 18:27:23
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    SubmitSummaryVO getSubmitSummary(HttpServletRequest request);

    QuestionRunResult doQuestionRun(QuestionRunRequest questionRunRequest);
}
