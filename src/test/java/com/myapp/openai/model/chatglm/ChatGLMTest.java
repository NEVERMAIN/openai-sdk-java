package com.myapp.openai.model.chatglm;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.myapp.openai.executor.model.chatglm.utils.BearerTokenUtils;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMCompletionRequest;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMCompletionResponse;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.session.OpenAiConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class ChatGLMTest {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException {

        // 1.配置类
        OpenAiConfiguration openAiConfiguration = new OpenAiConfiguration();
        ChatGLMConfig chatGLMConfig = new ChatGLMConfig();
        chatGLMConfig.setApiHost("https://open.bigmodel.cn/");
        chatGLMConfig.setApiSecretKey("6fbc6c46a84a201fad03b6de40253619.2WNgqPQhA34dEEoq");

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 创建  okHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    // 从请求中获取 token 参数,并将其添加到请求路径中
                    Request request = original.newBuilder()
                            .url(original.url())
                            .header("Authorization", BearerTokenUtils.getToken("6fbc6c46a84a201fad03b6de40253619", "2WNgqPQhA34dEEoq"))
                            .header("Content-Type", "application/json")
                            .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
                            // 请求体内容
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(openAiConfiguration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(openAiConfiguration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(openAiConfiguration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        // sse Factory
        EventSource.Factory factory = EventSources.createFactory(okHttpClient);

        // 创建入参
        ChatGLMCompletionRequest chatGLMCompletionRequest = new ChatGLMCompletionRequest();
        chatGLMCompletionRequest.setModel(CompletionRequest.Model.GLM_3_TURBO.getCode());
        chatGLMCompletionRequest.setStream(true);
        chatGLMCompletionRequest.setMessages(new ArrayList<ChatGLMCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatGLMCompletionRequest.Prompt.builder()
                        .role("user")
                        .content("1+1等于多少")
                        .build());

            }
        });

        Request request = new Request.Builder()
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV4_COMPLETIONS()))
                .post(RequestBody.create(MediaType.parse(OpenAiConfiguration.JSON_CONTENT_TYPE), new ObjectMapper().writeValueAsString(chatGLMCompletionRequest)))
                .build();

        // 1. 发起请求
        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @javax.annotation.Nullable String id, @javax.annotation.Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    return;
                }
                ChatGLMCompletionResponse response = JSON.parseObject(data, ChatGLMCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response.getChoices()));

            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                log.info("对话完毕");
            }
            @Override
            public void onFailure(@NotNull EventSource eventSource, @javax.annotation.Nullable Throwable t, @Nullable Response response) {
                log.error("对话失败", t);

            }
        });


    }
}
