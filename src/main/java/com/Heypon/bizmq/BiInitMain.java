package com.Heypon.bizmq;

import com.Heypon.constant.BiMqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class BiInitMain {

    @SneakyThrows
    public static void main(String[] args) {
        try{

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // 声明交换机
            String exchange = BiMqConstant.BI_EXCHANGE_NAME;
            channel.exchangeDeclare(exchange, "direct", true);

            // 设定队列参数
            Map<String, Object> queueArgs = new HashMap<>();
            // 最大消息数量
            queueArgs.put("x-max-length", 1000);
            // 声明队列
            String queue = BiMqConstant.BI_QUEUE_NAME;
            // 参数：队列名、是否持久化、是否独占、是否自动删除、队列参数
            channel.queueDeclare(queue, true, false, false, queueArgs);
            channel.queueBind(queue, exchange, BiMqConstant.BI_ROUTING_KEY);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
