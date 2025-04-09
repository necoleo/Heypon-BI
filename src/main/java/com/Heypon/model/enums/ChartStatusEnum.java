package com.Heypon.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图表状态枚举类
 */
public enum ChartStatusEnum {

    WAIT("等待","WAIT"),
    RUNNING("生成中","RUNNING"),
    SUCCEED("成功","SUCCEED"),
    FAILED("失败","FAILED");

    private final String text;
    private final String value;

    ChartStatusEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ChartStatusEnum getEnumByValue(String value){
        if (ObjectUtils.isEmpty(value)){
            return null;
        }
        for (ChartStatusEnum anEnum : ChartStatusEnum.values()){
            if (anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

}
