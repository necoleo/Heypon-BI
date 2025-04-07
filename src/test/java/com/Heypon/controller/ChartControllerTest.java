package com.Heypon.controller;

import com.Heypon.model.dto.chart.ChartQueryRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartControllerTest {

    @Test
    void listMyChartVOByPage() {
        ChartQueryRequest request = new ChartQueryRequest();
        request.setUserId(1904115286207115265L);

    }
}