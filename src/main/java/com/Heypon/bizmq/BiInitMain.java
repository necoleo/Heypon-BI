package com.Heypon.bizmq;

import com.Heypon.constant.BiMqConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
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

            // 声明队列
            String queue = BiMqConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, BiMqConstant.BI_ROUTING_KEY);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
