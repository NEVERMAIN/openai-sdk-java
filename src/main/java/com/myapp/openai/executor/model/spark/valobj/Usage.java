package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Usage {

    private Text text;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Text {
        /**
         * 保留字段，可忽略
         */
        @JsonProperty("question_tokens")
        private Integer questionTokens;
        /**
         * 包含历史问题的总 tokens 大小
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        /**
         * 回答的 tokens 大小
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        /**
         * prompt_tokens 和 completion_tokens 的和，也是本次交互计费的 tokens 大小
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;

    }



}
