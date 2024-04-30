package com.myapp.openai.session.defaults;

import com.alibaba.fastjson.JSON;
import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
import com.myapp.openai.executor.parameter.response.ChatChoice;
import com.myapp.openai.executor.parameter.response.CompletionResponse;
import com.myapp.openai.executor.parameter.response.ImageResponse;
import com.myapp.openai.session.OpenAiConfiguration;
import com.myapp.openai.session.OpenAiSession;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @description: 默认的会话模型
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class DefaultOpenAiSession implements OpenAiSession {

    private final OpenAiConfiguration openAiConfiguration;
    private final Map<String, Executor> executorGroup;

    public DefaultOpenAiSession(OpenAiConfiguration openAiConfiguration, Map<String, Executor> executorGroup) {
        this.openAiConfiguration = openAiConfiguration;
        this.executorGroup = executorGroup;
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 1.选择执行器
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) {
            throw new RuntimeException(completionRequest.getModel() + " 模型执行器未实现 ");
        }
        // 2.执行结果
        return executor.completions(completionRequest, eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 1.选择执行器
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) {
            throw new RuntimeException(completionRequest.getModel() + " 模型执行器未实现 ");
        }
        // 2.执行结果
        return executor.completions(apiHostByUser, apiKeyByUser, completionRequest, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(CompletionRequest completionRequest) throws Exception {

        // 1.用户执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder dataBuffer = new StringBuilder();
        completions(completionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    future.complete(dataBuffer.toString());
                    return;
                }

                CompletionResponse completionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = completionResponse.getChoices();
                for (ChatChoice choice : choices) {
                    Message delta = choice.getDelta();
                    if (!CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNotBlank(finishReason) && "stop".equalsIgnoreCase(finishReason)) {
                        dataBuffer.append(delta.getContent());
                        future.complete(dataBuffer.toString());
                        return;
                    }

                    // 填充数据
                    dataBuffer.append(delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完毕");
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }


    @Override
    public CompletableFuture<String> completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest) throws Exception {

        // 1.用户执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder dataBuffer = new StringBuilder();
        completions(apiHostByUser, apiKeyByUser, completionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    future.complete(dataBuffer.toString());
                    return;
                }

                CompletionResponse completionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = completionResponse.getChoices();
                for (ChatChoice choice : choices) {
                    Message delta = choice.getDelta();
                    if (!CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNotBlank(finishReason) && "stop".equalsIgnoreCase(finishReason)) {
                        dataBuffer.append(delta.getContent());
                        future.complete(dataBuffer.toString());
                        return;
                    }

                    // 填充数据
                    dataBuffer.append(delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完毕");
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;

    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        // 1.选择执行器
        Executor executor = executorGroup.get(imageRequest.getModel());
        if(null == executor) {
            throw new RuntimeException(imageRequest.getModel() + " 模型执行器未实现 ");
        }
        // 2.执行结果
        return executor.genImages(imageRequest);
    }
}
