package com.myapp.openai.model.chatglm;

import com.alibaba.fastjson.JSON;
import com.myapp.openai.executor.model.chatglm.config.ChatGLMConfig;
import com.myapp.openai.executor.model.chatglm.valobj.ChatGLMCompletionResponse;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() throws IOException {

        ChatGLMConfig chatGLMConfig = new ChatGLMConfig();
        chatGLMConfig.setApiHost("https://open.bigmodel.cn/");
        chatGLMConfig.setApiSecretKey("6fbc6c46a84a201fad03b6de40253619.2WNgqPQhA34dEEoq");

        // 2. 配置文件
        OpenAiConfiguration openAiConfiguration = new OpenAiConfiguration();
        openAiConfiguration.setLevel(HttpLoggingInterceptor.Level.BODY);
        openAiConfiguration.setChatGLMConfig(chatGLMConfig);

        // 3.会话工厂
        DefaultOpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(openAiConfiguration);
        // 4.开启一个会话
        openAiSession = factory.openSession();
    }

    @Test
    public void test_completions() throws Exception {

        List<Message> arrayList = new ArrayList<>();
        arrayList.add(Message.builder().role(CompletionRequest.Role.USER).content("1+1等于多少").build());
        // 1.创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(arrayList)
                .model(CompletionRequest.Model.GLM_3_TURBO.getCode())
                .build();

        // 2. 请求参数
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 3.应答请求
        EventSource completions = openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, String data) {

                if ("[DONE]".equals(data)) {
                    log.info("OpenAi 应答完成");
                    return;
                }

                ChatGLMCompletionResponse completionResponse = JSON.parseObject(data, ChatGLMCompletionResponse.class);
                List<ChatGLMCompletionResponse.Choice> choices = completionResponse.getChoices();
                for (ChatGLMCompletionResponse.Choice choice : choices) {
                    ChatGLMCompletionResponse.Delta delta = choice.getDelta();
                    if (!CompletionRequest.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNotBlank(finishReason) && "stop".equals(finishReason)) {
                        if(StringUtils.isBlank(delta.getContent())){
                            return;
                        }
                    }
                    log.info("测试结果:{}", delta.getContent());
                }
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("对话出现异常", t);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

    }

    @Test
    public void test_image() throws Exception {
        ImageRequest imageRequest = ImageRequest.builder()
                .model(CompletionRequest.Model.CogView.getCode())
                .prompt("画一幅油画,希望你画出教父的样子")
                .userId(UUID.randomUUID().toString())
                .build();

        ImageResponse imageResponse = openAiSession.genImages(imageRequest);
        log.info("收到的图片:{}",imageResponse);
    }

}
