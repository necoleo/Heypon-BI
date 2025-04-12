package com.Heypon.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class BiConsumerTest {

    @Resource
    private BiProducer biProducer;

    @Test
    void receiveMessage() {
        biProducer.sendMessage("你好哈");
    }
}