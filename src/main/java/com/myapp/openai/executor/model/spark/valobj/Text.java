package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Text {
    /**
     * system 用于设置对话背景，user表示是用户的问题，assistant表示 AI的回复
     */
    private String role;
    /**
     * 用户和 AI 的对话内容
     * 所有 content 的累计 tokens 需控制 8192 以内
     */
    private String content;
}

