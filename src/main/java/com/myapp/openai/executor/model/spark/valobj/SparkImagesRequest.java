package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SparkImagesRequest {

    private Header header;
    private Parameter parameter;
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Header {
        /** 应用 appid，从开放平台控制台创建的应用中获取 */
        @JsonProperty("app_id")
        private String appId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameter {
        private ImagesChat chat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Payload {
        private ImagesMessage message;
    }

}
