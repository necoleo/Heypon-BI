package com.Heypon.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.Heypon.common.ErrorCode;
import com.Heypon.constant.DeepSeekConstant;
import com.Heypon.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
public class DeepSeekApiManager {

//     Deepseek api key
    @Value("${deepseek.apiKey}")
    private String deepseekApiKey;

    private final static String API_URL = "https://api.deepseek.com/v1/chat/completions";

    /**
     * 创建对话
     * @param message 对话信息
     * @return
     */
    public String doChat(String message) {
        JSONObject requestBody = bulidRequestBody(message);
        /**
         * 执行请求
         */
        try{
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Content-Type","application/json")
                    .header("Authorization", "Bearer " + deepseekApiKey)
                    .body(requestBody.toString())
                    .execute();

            if (response.getStatus() != 200) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, response.getStatus() + "AI 响应错误, " + response.body());
            }

            JSONObject responseJson = JSONUtil.parseObj(response.body());
            System.out.println(responseJson);
            JSONArray choices = responseJson.getJSONArray("choices");
            if (choices.isEmpty()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "API 返回结果为空");
            }

            // 提取生成的文本
            String result = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");

            return result;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 构造请求体
     * @param message 对话信息
     * @return
     */
    private JSONObject bulidRequestBody(String message){
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", DeepSeekConstant.DEEPSEEK_MODEL_R1);

        JSONArray messages = new JSONArray();
        // 预设信息
        String systemPrompt = "\"你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容:\\n\" +\n" +
                "                \"分析需求：\\n\" +\n" +
                "                \"{数据分析的需求或者目标}\\n\" +\n" +
                "                \"原始数据：\\n\" +\n" +
                "                \"{csv格式的原始数据，用,作为分隔符}\\n\" +\n" +
                "                \"请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\\n\" +\n" +
                "                \"【【【【【\\n\" +\n" +
                "                \"{前端 Echarts V5 的 option 配置对象的json代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\\n\" +\n" +
                "                \"【【【【【\\n\" +\n" +
                "                \"{明确的数据分析结论、越详细越好，不要生成多余的注释}\"";
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", systemPrompt);
        messages.add(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", message);
        messages.add(userMessage);

        requestBody.set("messages", messages);

        return requestBody;
    }

}