package com.myapp.openai.executor.parameter.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.openai.executor.parameter.Message;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 对话请求信息
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRequest implements Serializable {

    private static final long serialVersionUID = 2447852191771534038L;

    /**
     * 模型字段
     */
    private String model;

    /**
     * 请求参数 {"role":"user","content":"你好"}
     */
    private List<Message> messages;

    /**
     * 是否流式处理
     * 使用同步调用时，此参数应当设置为 false 或者省略。表示模型生成完所有内容后一次性返回所有内容。
     * 如果设置为 True，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     */
    @Builder.Default
    private Boolean stream = false;

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
    @Builder.Default
    private Double topP = 0.7D;

    /**
     * 采样温度，控制输出的随机性，必须为正数
     * 取值范围是：(0.0,1.0]，不能等于 0，默认值为 0.95。
     * 值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     * 建议您根据应用场景调整 top_p 或 temperature 参数,但不要同时调整两个参数
     */
    @Builder.Default
    private Double temperature = 0.95D ;
    /**
     * 模型输出最大 tokens，最大输出为 8192，默认值为 1024
     */
    @JsonProperty("max_token")
    @Builder.Default
    private Integer maxTokens = 2048;
    /**
     * 模型在遇到 stop 所制定的字符时将停止生成，目前仅支持单个停止词，格式为 ["stop_word1"]
     */
    private List<String> stop;
    /**
     * 终端用户的唯一ID，协助平台对终端用户的违规行为、生成违法及不良信息或其他滥用行为进行干预
     */
    @JsonProperty("user_id")
    private String userId;


    @Getter
    @AllArgsConstructor
    public enum Role{
        /**
         * 系统预设
         */
        SYSTEM("system"),
        /**
         * 用户
         */
        USER("user"),
        /**
         * AI回答
         */
        ASSISTANT("assistant"),
        MODEL("model"),

        USER_INFO("user_info"),
        BOT_INFO("bot_info"),
        BOT_NAME("bot_name"),
        USER_NAME("user_name"),

        ;
        private final String code;

        /**
         * 获取枚举值
         */
        public static Role get(String code){
            Role[] values = Role.values();
            for (Role value : values) {
                if(value.getCode().equals(code)){
                    return value;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Model{

        /**
         * ChatGLM
         */
        /**
         * glm-3-turbo 大模型
         * 推荐使用 SSE 或异步调用方式请求接口
         */
        GLM_3_TURBO("glm-3-turbo"),
        /**
         * GLM-4
         * 推荐使用 SSE 或异步调用方式请求接口
         */
        GLM_4("glm-4"),
        /**
         * GLM-4V
         * 推荐使用 SSE 或同步调用方式请求接口
         */
        GLM_4V("glm-4v"),
        CogView("cogview-3"),
        /**
         * ChatGLM-超拟人大模型
         */
        CHARGLM_3("charglm-3"),

        /**
         * 讯飞星火大模型
         */
        /**
         * 聊天模型
         */
        GENERALV_3_5("generalv3.5"),
        /**
         * 图片生成
         */
        GENERAL("general"),


        ;

        private final String code;


        /**
         * 获取枚举值
         * @param code
         * @return
         */
        public static Model get(String code){
            Model[] values = Model.values();
            for (Model value : values) {
                if(value.getCode().equals(code)){
                    return value;
                }
            }
            return null;
        }
    }



}
