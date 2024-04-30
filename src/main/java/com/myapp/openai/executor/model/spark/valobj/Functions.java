package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Functions {

    private List<Text> text;

    /**
     * 列表形式，列表中的元素是json格式
     * 元素中包含name、description、parameters属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder

    public static class Text {
        /**
         * 用户输入命中后，会返回该名称
         */
        private String name;
        /**
         * 描述function功能即可，越详细越有助于大模型理解该function
         */
        private String description;
        /**
         * 含 type、properties、required字段
         */
        private Parameters parameters;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Parameters {
        /**
         * 参数类型
         */
        private String type = "object";
        /**
         * 参数信息描述
         * 该内容由用户定义，命中该方法时需要返回哪些参数
         */
        private Properties properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Properties {
        private Location location;
        private Date date;
        private List<Required> required;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        private String type = "string";
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Date {
        private String type = "string";
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Required {
        private String location;

    }
}
