package com.myapp.openai.executor.model.chatglm.valobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: ChatCLM 请求参数
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGLMCompletionRequest {

    /**
     * 模型字段
     */
    private String model;

    /**
     * 请求参数 {"role":"user","content":"你好"}
     */
    private List<Prompt> messages;

    /**
     * 是否流式处理
     * 使用同步调用时，此参数应当设置为 false 或者省略。表示模型生成完所有内容后一次性返回所有内容。
     * 如果设置为 True，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     */
    private Boolean stream;

    /**
     * do_sample 为 true 时启用采样策略，
     * do_sample 为 false 时采样策略 temperature、top_p 将不生效
     */
    @JsonProperty("do_sample")
    private String doSample;

    /**
     * 用温度取样的另一种方法，称为核取样
     */
    @JsonProperty("top_p")
    private Double topP = 0.7D;

    /**
     * 采样温度，控制输出的随机性，必须为正数
     * 取值范围是：(0.0,1.0]，不能等于 0，默认值为 0.95。
     * 值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     * 建议您根据应用场景调整 top_p 或 temperature 参数,但不要同时调整两个参数
     */
    private Double temperature = 0.95D ;


    /**
     * 模型输出最大 tokens，最大输出为 8192，默认值为 1024
     */
    @JsonProperty("max_token")
    private Integer maxTokens = 1024;

    /**
     * 模型在遇到 stop 所制定的字符时将停止生成，目前仅支持单个停止词，格式为 ["stop_word1"]
     */
    private List<String> stop;

    /**
     * 可供模型调用的工具列表,tools字段会计算 tokens,同样受到 tokens 长度的限制
     */
    private List<Tool> tools;


    /**
     * 用于控制模型是如何选择要调用的函数，仅当工具类型为 function 时补充。
     * 默认为 auto，当前仅支持 auto。
     */
    @JsonProperty("tool_choice")
    private String toolChoice = "auto";



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Prompt{
        private String role;
        private String content;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Tool{

        /**
         * 工具类型,目前支持 function、retrieval、web_search
         */
        private String type;

        /**
         * 仅当工具类型为 function 时补充
         */
        private Function function;

        /**
         * 仅当工具类型为 retrieval 时补充
         */
        private Retrieval retrieval;

        /**
         * 仅当工具类型为 web_search 时补充。
         */
        @JsonProperty("web_search")
        private webSearch webSearch;

    }

    /**
     * 仅当工具类型为 function 时补充
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Function{
        /**
         * 函数名称，只能包含 a-z，A-Z，0-9，下划线和中横线。最大长度限制为64
         */
        private String name;
        /**
         * 用于描述函数功能。模型会根据这段描述决定函数调用方式。
         */
        private String description;
        /**
         * parameter 字段需要传入一个 Json Schema 对象，以准确地定义函数所接受的参数。
         * 若调用函数时不需要传入参数，省略该参数即可
         */
        private Object parameters;

    }

    /**
     * 仅当工具类型为 retrieval 时补充
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Retrieval{
        /**
         * 当涉及到知识库ID时，请前往开放平台的知识库模块进行创建或获取
         */
        @JsonProperty("knowledge_id")
        private String knowledgeId;

        @JsonProperty("prompt_template")
        private String promptTemplate;
    }

    /**
     * 仅当工具类型为 web_search 时补充。
     * 如果 tools 中存在类型 retrieval,此时 web_search 不生效。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class webSearch{

        /**
         * 是否启用搜索，默认启用搜索
         * 启用：true
         * 禁用：false
         */
        private Boolean enable;
        /**
         * 强制搜索自定义关键内容，此时模型会根据自定义搜索关键内容返回的结果作为背景知识来回答用户发起的对话。
         */
        @JsonProperty("search_query")
        private String searchQuery;

    }
}
