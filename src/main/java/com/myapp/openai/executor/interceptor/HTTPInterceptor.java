package com.myapp.openai.executor.interceptor;

import com.myapp.openai.executor.model.chatglm.utils.BearerTokenUtils;
import com.myapp.openai.session.OpenAiConfiguration;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @description: 请求拦截器
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public class HTTPInterceptor implements Interceptor {

    /**
     * 智普 Ai，Jwt 加密 Token
     */
    private final OpenAiConfiguration openAiConfiguration;

    public HTTPInterceptor(OpenAiConfiguration openAiConfiguration) {
        this.openAiConfiguration = openAiConfiguration;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 1. 获取原始 Request
        Request original = chain.request();
        // 2. 构建请求
        Request request = original.newBuilder()
                .url(original.url())
                .header("Content-Type", OpenAiConfiguration.APPLICATION_JSON)
                .header("User-Agent", OpenAiConfiguration.DEFAULT_USER_AGENT)
                .header("Accept", OpenAiConfiguration.SSE_CONTENT_TYPE)
                .method(original.method(), original.body())
                .build();
        // 3. 返回执行结果
        return chain.proceed(request);
    }

}