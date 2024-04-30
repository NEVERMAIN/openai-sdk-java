package com.myapp.openai.model.spark;

import com.alibaba.fastjson.JSON;
import com.myapp.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.myapp.openai.executor.model.spark.config.SparkConfig;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
import com.myapp.openai.executor.parameter.response.ChatChoice;
import com.myapp.openai.executor.parameter.response.CompletionResponse;
import com.myapp.openai.executor.parameter.response.ImageResponse;
import com.myapp.openai.session.OpenAiConfiguration;
import com.myapp.openai.session.OpenAiSession;
import com.myapp.openai.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class SparkTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() throws IOException {

        SparkConfig sparkConfig = new SparkConfig();
        sparkConfig.setAppid("dac4535e");
        sparkConfig.setApiKey("d984fda9834da3c4786df752f9714266");
        sparkConfig.setApiSecret("OTlmMTI4ZTlhZmM1NzY0YmViZWNmNzc0");


        // 2. 配置文件
        OpenAiConfiguration openAiConfiguration = new OpenAiConfiguration();
        openAiConfiguration.setLevel(HttpLoggingInterceptor.Level.BODY);
        openAiConfiguration.setSparkConfig(sparkConfig);
        // 3.会话工厂
        DefaultOpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(openAiConfiguration);
        // 4.开启一个会话
        openAiSession = factory.openSession();
    }

    @Test
    public void test_completion() throws Exception {

        // 1.创建参数
        CompletionRequest completionRequest = CompletionRequest.builder()
                .stream(true)
                .model(CompletionRequest.Model.GENERALV_3_5.getCode())
                .messages(new ArrayList<Message>(){{
                    add(Message.builder().role(CompletionRequest.Role.SYSTEM).content("你现在扮演李白，你豪情万丈，狂放不羁；接下来请用李白的口吻和用户对话。").build());
                    add(Message.builder().role(CompletionRequest.Role.USER).content("你会做什么").build());
                }})
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 2.应答请求
        EventSource completions = openAiSession.completions(completionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {

                // 1.解析
                CompletionResponse completionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = completionResponse.getChoices();
                for (ChatChoice choice : choices) {
                    Message delta = choice.getDelta();
                    if(!CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;
                    log.info("测试结果:{}",delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("对话出现异常", t);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

    }

//    @Test
//    public void test_images() throws Exception {
//        // 1.构建参数
//        ImageRequest imageRequest = ImageRequest.builder()
//                .model(CompletionRequest.Model.GENERAL.getCode())
//                .prompt("画一只小猫咪")
//                .build();
//
//        // 2.发起请求
//        ImageResponse imageResponse = openAiSession.genImages(imageRequest);
//        log.info("图片:{}",imageResponse.getData());
//    }


}
