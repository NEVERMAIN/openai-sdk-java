package com.myapp.openai.executor.result;

import okhttp3.sse.EventSourceListener;

/**
 * @description: 结果处理器
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public interface ResultHandler {

    /**
     * 统一返回结果
     * @param eventSourceListener 事件监听器
     * @return 事件监听器
     */
    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);
}
