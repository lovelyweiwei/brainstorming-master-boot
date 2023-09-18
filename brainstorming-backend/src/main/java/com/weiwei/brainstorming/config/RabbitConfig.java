package com.weiwei.brainstorming.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq 配置文件
 *
 * @Author weiwei
 * @Date 2023/7/6 23:33
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange directExchange() {
        return ExchangeBuilder.directExchange("oj_exchange").build();
    }

    @Bean
    public Queue queue() {
        Map<String, Object> arguments = new HashMap<>();
        //设置对列的最大长度
        arguments.put("x-max-length", 20);
        arguments.put("x-message-ttl", 5000); //设置对列的过期时间
        //设置对列的死信交换机
        arguments.put("x-dead-letter-exchange", "oj_dlx_exchange");
        //设置死信路由key，要和死信交换机和死信队列绑定key一模一样，因为死信交换机是直连交换机
        arguments.put("x-dead-letter-routing-key", "error");
        return QueueBuilder.durable("oj_queue")
                .withArguments(arguments) // 设置对列的参数
                .build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return ExchangeBuilder.directExchange("oj_dlx_exchange").build();
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable("oj_dlx_queue").build();
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("oj");
    }

    @Bean
    public Binding bindingDlx(DirectExchange dlxExchange, Queue dlxQueue) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with("error");
    }
}
