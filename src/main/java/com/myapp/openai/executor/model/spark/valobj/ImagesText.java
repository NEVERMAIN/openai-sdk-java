package com.myapp.openai.executor.model.spark.valobj;

import lombok.*;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagesText {
    /**
     * system 用于设置对话背景，user表示是用户的问题，assistant表示 AI的回复
     */
    private String role;
    /**
     * 用户和 AI 的对话内容
     * 所有 content 的累计 tokens 需控制 8192 以内
     */
    private String content;

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
