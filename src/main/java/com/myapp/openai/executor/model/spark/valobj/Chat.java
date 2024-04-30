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
public class Chat {

    /**
     * 指定访问的领域:
     * 注意：不同的取值对应的 url 也不一样！
     */
    private String domain = "generalv3.5";
    /**
     * 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
     */
    private Double temperature = 0.5D;
    /**
     * 模型回答的tokens的最大长度
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 2048;
    /**
     * 从k个候选中随机选择⼀个（⾮等概率）
     */
    @JsonProperty("top_k")
    private Integer topK = 4;
    /**
     * 用于关联用户会话
     */
    @JsonProperty("chat_id")
    private String chatId;
}
