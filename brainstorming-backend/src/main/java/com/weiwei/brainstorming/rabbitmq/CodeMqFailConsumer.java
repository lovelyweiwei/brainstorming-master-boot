package com.weiwei.brainstorming.rabbitmq;

import com.weiwei.brainstorming.common.ErrorCode;
import com.weiwei.brainstorming.exception.BusinessException;
import com.weiwei.brainstorming.judge.codesandbox.model.JudgeInfo;
import com.weiwei.brainstorming.model.entity.QuestionSubmit;
import com.weiwei.brainstorming.model.enums.QuestionSubmitStatusEnum;
import com.weiwei.brainstorming.service.QuestionSubmitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import org.springframework.messaging.handler.annotation.Header;

import javax.annotation.Resource;

/**
 * 判题死信队列
 *
 * @author weiwei
 */
@Component
@Slf4j
public class CodeMqFailConsumer {

    @Resource
    private QuestionSubmitService questionSubmitService;


    /**
     * 监听死信队列
     *
     * @param message
     * @param channel
     * @param dekivery
     */
    @SneakyThrows
    @RabbitListener(queues = {"oj_dlx_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long dekivery) {
        // 接收到失败的信息
        log.info("死信队列接受到的消息：{}", message);
        if (StringUtils.isBlank(message)) {
            channel.basicNack(dekivery, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息为空");
        }
        long questionSubmitId = Long.parseLong(message);
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);

        if (questionSubmit == null) {
            channel.basicNack(dekivery, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交的题目信息不存在");
        }
        // 把提交题目标为失败
        questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        questionSubmit.setJudgeInfo("{'message':'答案错误'}");

        boolean update = questionSubmitService.updateById(questionSubmit);
        if (!update) {
            log.info("处理死信队列消息失败,对应提交的题目id为:{}", questionSubmit.getId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "处理死信队列消息失败");
        }
        // 确认消息
        channel.basicAck(dekivery, false);
    }
}
