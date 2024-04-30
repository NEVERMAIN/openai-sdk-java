package com.myapp.openai.session;

import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.model.chatglm.ChatGLMModelExecutor;
import com.myapp.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.myapp.openai.executor.model.spark.SparkModelExecutor;
import com.myapp.openai.executor.model.spark.config.SparkConfig;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.HashMap;

/**
 * @description: 配置文件
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@Slf4j
public class OpenAiConfiguration {

    /**
     * 智谱Ai ChatGLM Config
     */
    private ChatGLMConfig chatGLMConfig;
    /**
     * 星火大模型
     */
    private SparkConfig sparkConfig;

    /**
     * OkHttpClient
     */
    private OkHttpClient okHttpClient;

    /**
     * 执行器工厂
     */
    private HashMap<String, Executor> executorGroup;

    /**
     * 创建 sse 请求工厂
     * @return
     */
   public EventSource.Factory createRequestFactory(){
       return EventSources.createFactory(okHttpClient);
   }

    /**
     * OkHttp 配置信息
     */
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
    private long connectTimeout = 4500;
    private long writeTimeout = 4500;
    private long readTimeout = 4500;
    /**
     * http keywords
     */
    public static final String SSE_CONTENT_TYPE = "text/event-stream";
    public static final String APPLICATION_JSON = "application/json";
    public static final String JSON_CONTENT_TYPE = APPLICATION_JSON + ";charset=utf-8";
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";

    /**
     * 创建执行器集合
     * @return
     */
    public HashMap<String,Executor> newExecutorGroup(){
        this.executorGroup = new HashMap<>();
        // ChatGLM 类型执行器填充
        ChatGLMModelExecutor chatGLMModelExecutor = new ChatGLMModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.GLM_3_TURBO.getCode(),chatGLMModelExecutor);
        executorGroup.put(CompletionRequest.Model.GLM_4.getCode(),chatGLMModelExecutor);
        executorGroup.put(CompletionRequest.Model.GLM_4V.getCode(),chatGLMModelExecutor);
        executorGroup.put(CompletionRequest.Model.CogView.getCode(),chatGLMModelExecutor);

        // 星火大模型
        SparkModelExecutor sparkModelExecutor = new SparkModelExecutor(this);
        executorGroup.put(CompletionRequest.Model.GENERALV_3_5.getCode(),sparkModelExecutor);
        executorGroup.put(CompletionRequest.Model.GENERAL.getCode(),sparkModelExecutor);



        return this.executorGroup;
    }
}
