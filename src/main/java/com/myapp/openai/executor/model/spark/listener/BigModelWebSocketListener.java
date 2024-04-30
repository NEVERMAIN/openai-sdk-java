package com.myapp.openai.executor.model.spark.listener;


import com.alibaba.fastjson.JSON;
import com.myapp.openai.executor.model.spark.valobj.Choices;
import com.myapp.openai.executor.model.spark.valobj.SparkCompletionRequest;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.response.ChatChoice;
import com.myapp.openai.executor.parameter.response.CompletionResponse;
import com.myapp.openai.executor.model.spark.valobj.SparkCompletionResponse;
import com.myapp.openai.executor.parameter.response.Usage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description: websocket 监听器
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class BigModelWebSocketListener extends WebSocketListener {

    private final SparkCompletionRequest request;

    private final EventSourceListener eventSourceListener;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final EventSource eventSource;

    /**
     * websocket 事件监听器
     *
     * @param request             请求对象
     * @param eventSourceListener sse 事件监听器
     */
    public BigModelWebSocketListener(SparkCompletionRequest request, EventSourceListener eventSourceListener) {
        this.request = request;
        this.eventSourceListener = eventSourceListener;
        this.eventSource = new EventSource() {
            @Override
            public Request request() {
                return this.request();
            }

            @Override
            public void cancel() {
                this.cancel();
            }
        };
    }

    /**
     * 建立连接后调用的方法
     *
     * @param webSocket websocket 连接
     * @param response  返回结果
     */
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        new Thread(() -> {
            webSocket.send(JSON.toJSONString(request));
            // 等待服务端返回完毕后关闭连接
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            webSocket.close(1000, "");
        }).start();
    }

    /**
     * 服务端传输消息时调用的方法
     *
     * @param webSocket websocket 连接
     * @param text      服务端传输的文本内容
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);

        // 1.解析 text
        SparkCompletionResponse response = JSON.parseObject(text, SparkCompletionResponse.class);

        // 反馈失败
        if (SparkCompletionResponse.Code.SUCCCESS.getValue() != response.getHeader().getCode()) {
            log.info("发生错误，错误码为：" + response.getHeader().getCode());
            log.info("本次请求的sid为：" + response.getHeader().getSid());
            countDownLatch.countDown();
        }

        // 封装参数
        CompletionResponse completionResponse = new CompletionResponse();
        completionResponse.setId(response.getHeader().getSid());
        completionResponse.setModel(CompletionRequest.Model.GENERALV_3_5.getCode());

        ArrayList<ChatChoice> chatChoices = new ArrayList<>();
        ChatChoice chatChoice = new ChatChoice();

        List<Choices.Text> textList = response.getPayload().getChoices().getText();
        for (Choices.Text t : textList) {
            chatChoice.setDelta(Message.builder()
                    .role(CompletionRequest.Role.ASSISTANT)
                    .content(t.getContent())
                    .build());
            chatChoices.add(chatChoice);
        }

        completionResponse.setChoices(chatChoices);

        Integer status = response.getPayload().getChoices().getStatus();
        if (SparkCompletionResponse.Status.START.getValue() == status) {
            eventSourceListener.onEvent(eventSource, "", "", JSON.toJSONString(completionResponse));
        } else if (SparkCompletionResponse.Status.ING.getValue() == status) {
            eventSourceListener.onEvent(eventSource, "", "", JSON.toJSONString(completionResponse));
        } else if (SparkCompletionResponse.Status.END.getValue() == status) {

            Usage usage = new Usage();
            com.myapp.openai.executor.model.spark.valobj.Usage.Text usageText = response.getPayload().getUsage().getText();
            usage.setTotalTokens(usageText.getTotalTokens());
            usage.setCompletionTokens(usageText.getCompletionTokens());
            usage.setPromptTokens(usageText.getPromptTokens());
            completionResponse.setUsage(usage);
            completionResponse.setCreated(System.currentTimeMillis());

            chatChoice.setFinishReason("stop");
            chatChoices.add(chatChoice);
            eventSourceListener.onEvent(eventSource, response.getHeader().getSid(), "", JSON.toJSONString(completionResponse));
            countDownLatch.countDown();

        }


    }

    /**
     * websocket 连接关闭时调用的方法
     *
     * @param webSocket websocket 连接
     * @param code
     * @param reason
     */
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        eventSourceListener.onClosed(eventSource);
    }

    /**
     * websocket 连接出现异常时调用的方法
     *
     * @param webSocket websocket 连接
     * @param t
     * @param response
     */
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        eventSourceListener.onFailure(eventSource, t, response);
    }


}
