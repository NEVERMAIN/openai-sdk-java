package com.myapp.openai.executor.model.chatglm;

import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.model.chatglm.valobj.CharGLMCompletionRequest;
import com.myapp.openai.executor.parameter.ParameterHandler;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
import com.myapp.openai.executor.parameter.response.ImageResponse;
import com.myapp.openai.executor.result.ResultHandler;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public class CharGLMModelExecutor implements Executor, ParameterHandler<CharGLMCompletionRequest> , ResultHandler {
    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return null;
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
        return null;
    }

    @Override
    public CharGLMCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        return null;
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return null;
    }
}
