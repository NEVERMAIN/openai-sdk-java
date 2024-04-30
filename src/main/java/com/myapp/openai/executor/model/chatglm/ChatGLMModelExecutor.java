package com.myapp.openai.executor.model.chatglm;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.myapp.openai.executor.model.chatglm.utils.BearerTokenUtils;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMCompletionRequest;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMCompletionResponse;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMImagesRequest;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.ParameterHandler;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
import com.myapp.openai.executor.parameter.response.ChatChoice;
import com.myapp.openai.executor.parameter.response.CompletionResponse;
import com.myapp.openai.executor.parameter.response.ImageResponse;
import com.myapp.openai.executor.parameter.response.Usage;
import com.myapp.openai.executor.result.ResultHandler;
import com.myapp.openai.session.OpenAiConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: ChatGLM 执行器
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class ChatGLMModelExecutor implements Executor, ParameterHandler<ChatGLMCompletionRequest>, ResultHandler {


    private final ChatGLMConfig chatGLMConfig;

    private final EventSource.Factory factory;

    private final OkHttpClient okHttpClient;

    public ChatGLMModelExecutor(OpenAiConfiguration openAiConfiguration) {
        this.chatGLMConfig = openAiConfiguration.getChatGLMConfig();
        this.factory = openAiConfiguration.createRequestFactory();
        this.okHttpClient = openAiConfiguration.getOkHttpClient();
    }

    /**
     * 执行会话
     *
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return
     * @throws Exception
     */
    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {

        // 1.转换参数
        ChatGLMCompletionRequest chatGLMCompletionRequest = getParameterObject(completionRequest);

        // 2.构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", BearerTokenUtils.getToken(chatGLMConfig.getApiKey(), chatGLMConfig.getApiSecret()))
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV4_COMPLETIONS()))
                .post(RequestBody.create(MediaType.parse(OpenAiConfiguration.JSON_CONTENT_TYPE), new ObjectMapper().writeValueAsString(chatGLMCompletionRequest)))
                .build();

        // 3.返回事件结果
        return factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        // 1.转换参数
        ChatGLMCompletionRequest chatGLMCompletionRequest = getParameterObject(completionRequest);

        // 2.自定义配置
        ChatGLMConfig chatGLMConfigByUser = new ChatGLMConfig();
        chatGLMConfigByUser.setApiHost(apiHostByUser);
        if (null != apiKeyByUser) {
            chatGLMConfigByUser.setApiSecretKey(apiKeyByUser);
        }

        String apiHost = (chatGLMConfigByUser.getApiHost() == null) ? chatGLMConfig.getApiHost() : chatGLMConfigByUser.getApiHost();
        String apiKey = (chatGLMConfigByUser.getApiKey() == null) ? chatGLMConfig.getApiKey() : chatGLMConfigByUser.getApiKey();
        String apiSecret = (chatGLMConfigByUser.getApiSecret() == null) ? chatGLMConfig.getApiSecret() : chatGLMConfigByUser.getApiSecret();

        // 3.构建请求信息
        Request request = new Request.Builder()
                .url(apiHost.concat(chatGLMConfig.getV4_COMPLETIONS()))
                .post(RequestBody.create(MediaType.parse(OpenAiConfiguration.APPLICATION_JSON), new ObjectMapper().writeValueAsString(chatGLMCompletionRequest)))
                .build();

        // 4.返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return genImages(null,null,imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
        // 1.统一转换参数
        ChatGLMImagesRequest chatGLMImagesRequest = ChatGLMImagesRequest.builder()
                .model(imageRequest.getModel())
                .prompt(imageRequest.getPrompt())
                .userId(imageRequest.getUserId())
                .build();

        // 2.动态设置 Host Key,便于用户传递自己的信息
        ChatGLMConfig chatGLMConfigByUser = new ChatGLMConfig();
        chatGLMConfigByUser.setApiHost(apiHostByUser);
        if (null != apiKeyByUser) {
            chatGLMConfigByUser.setApiSecretKey(apiKeyByUser);
        }
        String apiHost = (chatGLMConfigByUser.getApiHost() == null) ? chatGLMConfig.getApiHost() : apiHostByUser;
        String apiKey = (chatGLMConfigByUser.getApiKey() == null) ? chatGLMConfig.getApiKey() : chatGLMConfigByUser.getApiKey();
        String apiSecret = (chatGLMConfigByUser.getApiSecret() == null) ? chatGLMConfig.getApiSecret() : chatGLMConfigByUser.getApiSecret();

        // 3.构建请求信息
        Request request = new Request.Builder()
                .header("Authorization",BearerTokenUtils.getToken(apiKey,apiSecret))
                .url(apiHost.concat(chatGLMConfig.getV4_images()))
                .post(RequestBody.create(MediaType.parse(OpenAiConfiguration.JSON_CONTENT_TYPE),new ObjectMapper().writeValueAsString(chatGLMImagesRequest)))
                .build();

        // 4.返回结果
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        if(response.isSuccessful() && body != null){
            return JSON.parseObject(body.string(), ImageResponse.class);
        }else{
            throw new IOException("Failed to get image response");
        }

    }


    /**
     * 将统一参数转成模型入参
     *
     * @param completionRequest 对话请求信息
     * @return
     */
    @Override
    public ChatGLMCompletionRequest getParameterObject(CompletionRequest completionRequest) {


        ChatGLMCompletionRequest chatGLMCompletionRequest = new ChatGLMCompletionRequest();

        chatGLMCompletionRequest.setTemperature(completionRequest.getTemperature());
        chatGLMCompletionRequest.setTopP(completionRequest.getTopP());
        chatGLMCompletionRequest.setModel(completionRequest.getModel());
        chatGLMCompletionRequest.setStream(completionRequest.getStream());

        ArrayList<ChatGLMCompletionRequest.Prompt> prompts = new ArrayList<>();
        // 重新组装参数,ChatGLM 需要用 Okay 间隔历史消息
        List<Message> messages = completionRequest.getMessages();
        Message messageEntity = messages.remove(messages.size() - 1);
        for (Message message : messages) {
            String role = message.getRole();
            if (Objects.equals(role, CompletionRequest.Role.SYSTEM.getCode())) {

                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(CompletionRequest.Role.SYSTEM.getCode())
                        .content(message.getContent())
                        .build());
                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(CompletionRequest.Role.USER.getCode())
                        .content("Okay")
                        .build());
            } else {
                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(CompletionRequest.Role.USER.getCode())
                        .content(message.getContent())
                        .build());

                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(CompletionRequest.Role.USER.getCode())
                        .content("Okay")
                        .build());
            }
        }

        prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                .role(messageEntity.getRole())
                .content(messageEntity.getContent())
                .build());


        chatGLMCompletionRequest.setMessages(prompts);

        // 返回结果
        return chatGLMCompletionRequest;
    }

    /**
     * 统一返回值
     *
     * @param eventSourceListener 事件监听器
     * @return
     */
    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {

        return new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                // 对话结束
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    eventSourceListener.onEvent(eventSource, id, type, data);
                    return;
                }
                // 对话进行中
                // 1.解析参数
                ChatGLMCompletionResponse response = JSON.parseObject(data, ChatGLMCompletionResponse.class);
                ChatGLMCompletionResponse.Choice choice = response.getChoices().get(0);
                ChatGLMCompletionResponse.Delta delta = choice.getDelta();
                ChatGLMCompletionResponse.Usage responseUsage = response.getUsage();

                CompletionResponse completionResponse = new CompletionResponse();
                completionResponse.setId(response.getId());
                completionResponse.setCreated(response.getCreated());
                completionResponse.setModel(response.getModel());

                // 设置回答
                ArrayList<ChatChoice> choices = new ArrayList<>();
                ChatChoice chatChoice = new ChatChoice();
                chatChoice.setDelta(
                        Message.builder()
                                .role(Objects.requireNonNull(CompletionRequest.Role.get(delta.getRole())))
                                .content(delta.getContent())
                                .build()
                );
                choices.add(chatChoice);

                // 设置 tokens 统计
                Usage usage = new Usage();
                if (null != responseUsage) {
                    usage.setTotalTokens(responseUsage.getTotalTokens());
                    usage.setCompletionTokens(responseUsage.getCompletionTokens());
                    usage.setPromptTokens(responseUsage.getPromptTokens());
                }

                completionResponse.setChoices(choices);
                completionResponse.setUsage(usage);
                // 返回数据
                eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));

            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }
}
