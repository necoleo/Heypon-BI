package com.Heypon.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BiProducerTest {

    @Resource
    private BiProducer biProducer;

    @Test
    void sendMessage() {
        biProducer.sendMessage("1912618938345508865");
    }
}