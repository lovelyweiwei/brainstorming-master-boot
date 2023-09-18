package com.weiwei.brainstorming.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwei.brainstorming.common.BaseResponse;
import com.weiwei.brainstorming.common.ErrorCode;
import com.weiwei.brainstorming.common.ResultUtils;
import com.weiwei.brainstorming.exception.BusinessException;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.weiwei.brainstorming.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.weiwei.brainstorming.model.entity.Question;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.entity.User;
import com.weiwei.brainstorming.model.vo.QuestionSubmitVO;
import com.weiwei.brainstorming.service.QuestionSubmitService;
import com.weiwei.brainstorming.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 */
@RestController
//@RequestMapping("/question_submit")
@Slf4j
@Deprecated
@CrossOrigin
public class QuestionSubmitController {

    //@Resource
    //private QuestionSubmitService questionSubmitService;
    //
    //@Resource
    //private UserService userService;
    //
    ///**
    // * 提交题目
    // *
    // * @param questionSubmitAddRequest
    // * @param request
    // * @return resultNum 本次提交题目变化数
    // */
    //@PostMapping("/")
    //public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
    //                                           HttpServletRequest request) {
    //    if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //    }
    //    // 登录才能提交题目
    //    final User loginUser = userService.getLoginUser(request);
    //    long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
    //    return ResultUtils.success(questionSubmitId);
    //}
    //
    ///**
    // * 分页获取题目提交列表 （除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
    // *
    // * @param questionSubmitQueryRequest
    // * @param request
    // * @return
    // */
    //@PostMapping("/list/page")
    //public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
    //                                                                     HttpServletRequest request) {
    //    long current = questionSubmitQueryRequest.getCurrent();
    //    long size = questionSubmitQueryRequest.getPageSize();
    //    // 从数据库中查询原始的题目提交分页信息
    //    Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
    //            questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
    //    User loginUser = userService.getLoginUser(request);
    //    // 返回脱敏信息
    //    return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    //}

}
