package com.myapp.openai.executor.model.chatglm.valobj;

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
public class ChatGLMImagesRequest {

    /**
     * 模型名称
     */
    private String model;
    /**
     * 所需图像的文本描述
     */
    private String prompt;
    /**
     * 终端用户的唯一ID，协助平台对终端用户的违规行为、生成违法及不良信息或其他滥用行为进行干预
     */
    @JsonProperty("user_id")
    private String userId;
}
