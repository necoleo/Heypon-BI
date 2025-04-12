package com.Heypon.service;

import com.Heypon.model.dto.chart.ChartQueryRequest;
import com.Heypon.model.entity.Chart;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 92700
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2025-03-24 17:45:21
*/
public interface ChartService extends IService<Chart> {
    /**
     * 获取查询条件
     *
     * @param chartQueryRequest
     * @return
     */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);


}
