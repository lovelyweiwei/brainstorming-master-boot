package com.weiwei.brainstorming.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.weiwei.brainstorming.model.dto.question.QuestionQueryRequest;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.vo.QuestionVO;
import com.weiwei.brainstorming.model.vo.SafeQuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2023-08-31 18:26:25
 */
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    List<String> getQuestionTags();

    /**
     * 对象转包装类
     * @param question
     * @return
     */
    SafeQuestionVO objToVo(Question question, Long id);
}
