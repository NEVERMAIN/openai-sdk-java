package com.myapp.openai.executor.model.chatglm.valobj;

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
public class CharGLMCompletionRequest {

    /**
     * 模型
     */
    private String model;

    /**
     * 调用对话模型时，将当前对话信息列表作为提示输入给模型;
     * 按照 {"role": "user", "content": "你好"} 的键值对形式进行传参
     */
    private List<Prompt> prompt;
    /**
     * 角色及用户信息数据
     */
    private Meta meta;
    /**
     * 由用户端传参，需保证唯一性；用于区分每次请求的唯一标识，用户端不传时平台会默认生成
     */
    @JsonProperty("request_id")
    private String requestId;
    /**
     * 用于控制每次返回内容的类型，空或者没有此字段时默认按照json_string返回
     * - json_string 返回标准的 JSON 字符串
     * - text 返回原始的文本内容
     */
    @JsonProperty("return_type")
    private String returnType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Prompt{
        /**
         * 本条信息作者的角色，可选择 user 或 assistant
         */
        private String role;
        /**
         * 本条信息的具体内容
         */
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Meta{
        /**
         * 用户信息
         */
        @JsonProperty("user_info")
        private  String userInfo;
        /**
         * 角色信息
         */
        @JsonProperty("bot_info")
        private  String botInfo;
        /**
         * 角色名称
         */
        @JsonProperty("bot_name")
        private  String botName;
        /**
         * 用户名称，默认值为用户
         */
        @JsonProperty("user_name")
        private  String userName;
    }
}
