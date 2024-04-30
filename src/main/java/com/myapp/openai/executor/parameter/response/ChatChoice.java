package com.myapp.openai.executor.parameter.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.openai.executor.parameter.Message;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 当前对话的模型输出内容
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
public class ChatChoice implements Serializable {

    private static final long serialVersionUID = 802469482706186701L;
    /**
     * 结果下标
     */
    private long index;
    /**
     * stream = true 请求参数里返回的属性是 delta
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * stream = false 请求参数里返回的属性是 delta
     */
    @JsonProperty("message")
    private Message message;
    /**
     * 模型推理终止的原因.
     * stop : 代表推理自然结束或触发停止词.
     * tool_calls : 代表模型命中函数.
     * length : 代表到达 tokens 长度上限.
     * sensitive : 代表模型推理内容被安全审核接口拦截。请注意，针对此类内容，请用户自行判断并决定是否撤回已公开的内容.
     * network_error : 代表模型推理异常.
     */
    @JsonProperty("finish_reason")
    private String finishReason;

}