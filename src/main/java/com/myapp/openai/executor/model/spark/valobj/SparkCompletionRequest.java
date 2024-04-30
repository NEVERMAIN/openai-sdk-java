package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 星火大模型请求参数
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SparkCompletionRequest {

    private Header header;
    private Parameter parameter;
    private Payload payload;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        /**
         * 应用 appid，从开放平台控制台创建的应用中获取
         */
        @JsonProperty("app_id")
        private String appId;
        /**
         * 每个用户的id，用于区分不同用户
         */
        private String uid;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parameter {
        private Chat chat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private Message message;
        private Functions functions;
    }




}
