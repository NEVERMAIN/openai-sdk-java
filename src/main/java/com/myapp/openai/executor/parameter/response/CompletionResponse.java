package com.myapp.openai.executor.parameter.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 对话请求结果信息
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
public class CompletionResponse implements Serializable {

    private static final long serialVersionUID = -1156029608161718832L;

    /**
     * 任务 ID
     */
    private String id;
    /**
     * 请求创建时间，是以秒为单位的 Unix 时间戳。
     */
    private long created;
    /**
     * 对象
     */
    private String object;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 当前对话的模型输出内容
     */
    private List<ChatChoice> choices;
    /**
     * 结束时返回本次模型调用的 tokens 数量统计。
     */
    private Usage usage;
}
