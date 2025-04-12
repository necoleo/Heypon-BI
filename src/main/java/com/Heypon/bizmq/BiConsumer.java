package com.Heypon.bizmq;

import com.Heypon.common.ErrorCode;
import com.Heypon.constant.BiMqConstant;
import com.Heypon.exception.BusinessException;
import com.Heypon.exception.ThrowUtils;
import com.Heypon.manager.DeepSeekApiManager;
import com.Heypon.model.entity.Chart;
import com.Heypon.model.enums.ChartStatusEnum;
import com.Heypon.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * BI 项目消费者
 */
@Slf4j
@Component
public class BiConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DeepSeekApiManager deepSeekApi;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private ChartService chartService;

    /**
     * 指定程序监听的消息队列和确认机制
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到消息， chartId = {}", message);

        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);

        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        // 提交异步任务处理消息
        CompletableFuture.runAsync(() -> {
            boolean processResult = processChart(chart);
            ThrowUtils.throwIf(!processResult,ErrorCode.SYSTEM_ERROR, "调用AI接口失败");
            // 消息处理成功，确认消息
            try{
                channel.basicAck(deliveryTag, false);
            }catch (Exception e) {
                log.error("处理消息失败， chartId: {}", chartId, e);
                handleMessageFailure(chart, channel, deliveryTag, e.getMessage());
            }
        }, threadPoolExecutor).exceptionally(ex -> {
            // 处理异步任务异常
            log.error("异步任务执行失败 chartId: {}, 异常：{}", chartId, ex.getMessage(), ex);
            handleMessageFailure(chart, channel, deliveryTag, ex.getMessage());
            return null;
        });
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart){

        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String goalAndType = goal;
        if (StringUtils.isNotBlank(chartType)) {
            goalAndType += "，使用" + chartType;
        }
        userInput.append(goalAndType).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(chartData).append("\n");
        return userInput.toString();
    }

    private void updateChartStatus(long chartId, ChartStatusEnum status, String genChart, String genResult) {
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setChartStatus(status);
        if (genChart != null) {
            updateChart.setGenChart(genChart);
        }
        if (genResult != null) {
            updateChart.setGenResult(genResult);
        }
        boolean updateRes = chartService.updateById(updateChart);
        if (!updateRes) {
            log.error("更新图表状态失败，chartId: {}, status: {}", chartId, status);
        }
    }

    /**
     *
     * @param chart
     * @param channel
     * @param deliveryTag
     * @param exMessage
     */
    private void handleMessageFailure(Chart chart, Channel channel, long deliveryTag, String exMessage){
        try{
            // 更新图表状态为 失败
            updateChartStatus(chart.getId(), ChartStatusEnum.FAILED, null, null);
            // 拒绝消息
            channel.basicNack(deliveryTag, false,false);
            log.warn("消息处理失败，拒绝消息：chartId={},  原因：{}", chart.getId(), exMessage);
        }catch (Exception e){
            log.error("消息拒绝失败，chartId = {}, 异常信息：", chart.getId(), e);
        }
    }

    /**
     *
     * @param chart
     * @return
     */
    private boolean processChart(Chart chart) {
        boolean processResult = false;
        try{
            // 先把任务状态设置为 执行中
            updateChartStatus(chart.getId(), ChartStatusEnum.RUNNING, null,null);

            // 调用AI
            String aiResult = deepSeekApi.doChat(buildUserInput(chart));
            // 整理AI返回的数据
            String[] splitsRes = aiResult.split("【【【【【");
            if (splitsRes.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回结果格式错误");
            }
            String genChart = splitsRes[1].trim();
            String genResult = splitsRes[2].trim();
            // 更新图表状态为成功,并保存结果
            updateChartStatus(chart.getId(), ChartStatusEnum.SUCCEED, genChart, genResult);
            processResult = true;
        }catch (Exception e){
            log.error("调用 AI 接口出错, chartId: {}", chart.getId(), e);
        }
        return processResult;
    }
}
