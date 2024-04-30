package com.myapp.openai.executor.parameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 描述信息
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements Serializable {

    private static final long serialVersionUID = -2171962932857512264L;
    /**
     * 当前对话的角色,目前默认为 assistant(模型)
     */
    private String  role;
    /**
     * 当前对话的内容。命中函数时此字段为 null，未命中函数时返回模型推理结果。
     */
    private String content;
    private String name;
    /**
     * 模型生成的应调用函数的名称和参数。
     */
    @JsonProperty("tool_calls")
    private List<ToolCalls> toolCalls;

    public Message(){}

    private Message(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 建造者模式
     */
    public static final class Builder{
        private String role;
        private String content;
        private String name;

        public Builder(){}

        public Builder role(CompletionRequest.Role role){
            this.role  = role.getCode();
            return this;
        }
        public Builder role(String role){
            this.role  = role;
            return this;
        }


        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder name(String name){
            this.name =name;
            return this;
        }

        public Message build(){
            return new Message(this);
        }
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



}
