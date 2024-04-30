package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choices {
    /**
     * 文本响应状态，取值为[0,1,2]; 0代表首个文本结果；1代表中间文本结果；2代表最后一个文本结果
     */
    private Integer status;
    /**
     * 返回的数据序号，取值为[0,9999999]
     */
    private int seq;
    /**
     * 响应文本
     */
    private List<Text> text;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Text{
        /**
         * AI的回答内容
         */
        private String content;
        /**
         * 角色标识，固定为assistant，标识角色为AI
         */
        private String role;
        /**
         * 结果序号，取值为[0,10]; 当前为保留字段，开发者可忽略
         */
        private Integer index;
    }

    @Getter
    public enum Role {

        USER("user"),
        ASSISTANT("assistant"),
        ;

        Role(String name) {
            this.name = name;
        }

        private final String name;
    }

}
