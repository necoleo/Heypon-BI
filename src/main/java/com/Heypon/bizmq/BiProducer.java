package com.Heypon.bizmq;

import com.Heypon.constant.BiMqConstant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * BI 项目队列生产者
 */
@Component
public class BiProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        // 设置消息参数
        MessageProperties messageProperties = new MessageProperties();
        // 消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        // 构建消息体
        Message produceMessage = new Message(message.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, produceMessage);
    }


}
