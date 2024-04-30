package com.myapp.openai.executor.parameter;

import com.myapp.openai.executor.parameter.request.CompletionRequest;

/**
 * @description: 参数处理器,统一入参
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public interface ParameterHandler<T> {

    /**
     * 将统一的参数转化成模型需要的参数
     * @param completionRequest 统一一参数
     * @return 模型对应的参数
     */
    T getParameterObject(CompletionRequest completionRequest);
}
