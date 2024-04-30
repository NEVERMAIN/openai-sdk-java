package com.myapp.openai.session.defaults;

import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.interceptor.HTTPInterceptor;
import com.myapp.openai.executor.model.chatglm.utils.BearerTokenUtils;
import com.myapp.openai.session.OpenAiConfiguration;
import com.myapp.openai.session.OpenAiSession;
import com.myapp.openai.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory {

    private final OpenAiConfiguration openAiConfiguration;

    public DefaultOpenAiSessionFactory(OpenAiConfiguration openAiConfiguration) {
        this.openAiConfiguration = openAiConfiguration;
    }

    @Override
    public OpenAiSession openSession() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 创建  okHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new HTTPInterceptor(openAiConfiguration))
                .connectTimeout(openAiConfiguration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(openAiConfiguration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(openAiConfiguration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        openAiConfiguration.setOkHttpClient(okHttpClient);

        // 3. 创建执行器【模型 -> 映射执行器】
        HashMap<String, Executor> executorGroup = openAiConfiguration.newExecutorGroup();

        // 4. 创建会话服务
        return new DefaultOpenAiSession(openAiConfiguration, executorGroup);
    }
}
