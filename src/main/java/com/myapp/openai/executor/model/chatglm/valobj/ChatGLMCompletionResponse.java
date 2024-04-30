package com.myapp.openai.executor.model.chatglm.valobj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: ChatCLM 应答参数
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
public class ChatGLMCompletionResponse {

    /**
     * 任务 ID
     */
    private String id;
    /**
     * 请求创建时间，是以秒为单位的 Unix 时间戳。
     */
    private Integer created;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 当前对话的模型输出内容
     */
    private List<Choice> choices;
    /**
     * 结束时返回本次模型调用的 tokens 数量统计。
     */
    private Usage usage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice{
        /**
         * 结果下标
         */
        private Integer index;
        /**
         * 模型推理终止的原因.
         * stop : 代表推理自然结束或触发停止词。
         * tool_calls : 代表模型命中函数。
         * length : 代表到达 tokens 长度上限。
         * sensitive : 代表模型推理内容被安全审核接口拦截。请注意，针对此类内容，请用户自行判断并决定是否撤回已公开的内容。
         * network_error : 代表模型推理异常。
         */
        @JsonProperty("finish_reason")
        private String finishReason;
        /**
         * 模型返回的文本信息 & 流式响应
         */
        private Delta delta;

        /**
         * 模型返回的文本信息 & 同步调用
         */
        private Message message;
    }

    /**
     * 流式响应内容块
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Delta{
        /**
         * 当前对话的内容。命中函数时此字段为 null，未命中函数时返回模型推理结果。
         */
        private String content;
        /**
         * 当前对话的角色，目前默认为 assistant（模型）
         */
        private String role;
        /**
         * 模型生成的应调用函数的名称和参数。
         */
        @JsonProperty("tool_calls")
        private List<ToolCalls> toolCalls;
    }

    /**
     * 同步调用响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message{
        /**
         * 当前对话的内容。命中函数时此字段为 null，未命中函数时返回模型推理结果。
         */
        private String content;
        /**
         * 当前对话的角色，目前默认为 assistant（模型）
         */
        private String role;
        /**
         * 模型生成的应调用函数的名称和参数。
         */
        @JsonProperty("tool_calls")
        private List<ToolCalls> toolCalls;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCalls{
        /**
         * 命中函数的唯一标识符。
         */
        private String id;
        /**
         * 模型调用工具的类型，目前仅支持function。
         */
        private String type;
        private Function function;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function{
        /**
         * 模型生成的应调用函数的名称。
         */
        private String name;
        /**
         * 模型生成的 JSON 格式的函数调用参数。
         * 请注意，模型生成的 JSON 并不总是有效的，可能会出现函数模式未定义的参数。在调用函数之前，请在代码中验证参数。
         */
        private Object arguments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage{
        /**
         * 模型输入的 tokens 数量
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        /**
         * 用户输入的 tokens 数量
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        /**
         * 总 tokens 数量
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
