package com.myapp.openai.executor.model.spark;

import com.alibaba.fastjson.JSON;
import com.myapp.openai.executor.Executor;
import com.myapp.openai.executor.model.spark.config.SparkConfig;
import com.myapp.openai.executor.model.spark.listener.BigModelWebSocketListener;
import com.myapp.openai.executor.model.spark.utils.URLAuthUtils;
import com.myapp.openai.executor.model.spark.valobj.*;
import com.myapp.openai.executor.parameter.Message;
import com.myapp.openai.executor.parameter.ParameterHandler;
import com.myapp.openai.executor.parameter.request.CompletionRequest;
import com.myapp.openai.executor.parameter.request.ImageRequest;
import com.myapp.openai.executor.parameter.response.ImageResponse;
import com.myapp.openai.session.OpenAiConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
public class SparkModelExecutor implements Executor, ParameterHandler<SparkCompletionRequest> {


    /**
     * 配置文件
     */
    private final SparkConfig sparkConfig;
    /**
     * 客户端
     */
    private final OkHttpClient okHttpClient;

    private String appid;

    public SparkModelExecutor(OpenAiConfiguration openAiConfiguration) {
        this.sparkConfig = openAiConfiguration.getSparkConfig();
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
        return completions(null, null, completionRequest, eventSourceListener);
    }

    /**
     * 执行会话
     *
     * @param apiHostByUser       apiHost
     * @param apiKeyByUser        apiKey
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return
     * @throws Exception
     */
    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {

        // 1.核心参数校验
        if (!completionRequest.getStream()) {
            throw new RuntimeException("illegal parameter stream is false");
        }
        // 2.动态设置 Host、key 便于用户传递自己的信息
        String apiHost = (null == apiHostByUser) ? sparkConfig.getApiHost() : apiHostByUser;
        String authURL = getAuthURL(apiKeyByUser, apiHost);

        // 3.转换参数
        SparkCompletionRequest sparkCompletionRequest = getParameterObject(completionRequest);
        // 4.构建请求参数
        Request request = new Request.Builder()
                .url(authURL)
                .build();

        // 5.调用请求
        WebSocket webSocket = okHttpClient.newWebSocket(request, new BigModelWebSocketListener(sparkCompletionRequest, eventSourceListener));

        // 6.封装结果
        return new EventSource() {
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

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return genImages(null,null,imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {

        // 1.动态设置 Host key, 便于用户传递自己的信息
        String apiHost = (null == apiHostByUser) ? sparkConfig.getApiTtiHost() : apiHostByUser;
        String authURL = getAuthURL(apiKeyByUser, apiHost, "POST", Boolean.FALSE);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .model(imageRequest.getModel())
                .messages(new ArrayList<Message>() {{
                    add(Message.builder().role(CompletionRequest.Role.USER).content(imageRequest.getPrompt()).build());
                }})
                .temperature(0.5D)
                .build();

        // 2.转换参数
        SparkCompletionRequest sparkCompletionRequest = getParameterObject(completionRequest, CompletionRequest.Model.GENERAL.getCode());

        // 3.构建请求信息
        Request request = new Request.Builder()
                .url(authURL)
                .post(RequestBody.create(MediaType.parse("POST"), JSON.toJSONString(sparkCompletionRequest)))
                .build();

        // 4.执行请求
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            SparkCompletionResponse sparkCompletionResponse = JSON.parseObject(response.body().string(), SparkCompletionResponse.class);
            if (SparkCompletionResponse.Code.SUCCCESS.getValue() == sparkCompletionResponse.getHeader().getCode()) {
                SparkCompletionResponse.Payload payload = sparkCompletionResponse.getPayload();
                List<Choices.Text> texts = payload.getChoices().getText();

                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setCreated(System.currentTimeMillis());
                ArrayList<ImageResponse.Item> items = new ArrayList<>();
                imageResponse.setData(items);
                for (Choices.Text text : texts) {
                    ImageResponse.Item item = new ImageResponse.Item();
                    item.setUrl(base64ToImageUrl(text.getContent()));
                    imageResponse.getData().add(item);
                }
                return imageResponse;
            } else {
                log.error("生成图片失败，code:{},message:{}", sparkCompletionResponse.getHeader().getCode(), sparkCompletionResponse.getHeader().getMessage());
            }
        }
        return null;
    }

    /**
     * 构建聊天模型的请求参数
     *
     * @param completionRequest 请求参数
     * @param domain            模型类型
     * @return 星火对话模型使用的参数
     */
    public SparkCompletionRequest getParameterObject(CompletionRequest completionRequest, String domain) {

        // 1.头信息
        SparkCompletionRequest.Header header = getHeader();
        // 2.模型
        SparkCompletionRequest.Parameter parameter = SparkCompletionRequest.Parameter.builder()
                .chat(Chat.builder()
                        .domain(domain)
                        .temperature(completionRequest.getTemperature())
                        .maxTokens(completionRequest.getMaxTokens())
                        .build()).build();

        ArrayList<Text> texts = new ArrayList<>();
        List<Message> messages = completionRequest.getMessages();
        for (Message message : messages) {
            texts.add(Text.builder()
                    .role(message.getRole())
                    .content(message.getContent())
                    .build());
        }

        SparkCompletionRequest.Payload payload = SparkCompletionRequest.Payload.builder()
                .message(com.myapp.openai.executor.model.spark.valobj.Message.builder()
                        .text(texts)
                        .build())
                .build();

        // 返回结果
        return SparkCompletionRequest.builder()
                .header(header)
                .parameter(parameter)
                .payload(payload).build();

    }

    /**
     * 构建聊天模型的请求参数
     *
     * @param completionRequest 请求参数
     * @return
     */
    @Override
    public SparkCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        return getParameterObject(completionRequest, "generalv3.5");
    }

    /**
     * 得到鉴权的 URL
     *
     * @param apiKeyByUser 用户的 apiKey
     * @param apiHost      接口请求地址
     * @return
     * @throws Exception
     */
    private String getAuthURL(String apiKeyByUser, String apiHost) throws Exception {

        return getAuthURL(apiKeyByUser, apiHost, "GET", Boolean.TRUE);
    }

    /**
     * 得到鉴权的 URL
     *
     * @param apiKeyByUser 用户的 apiKey
     * @param apiHost      接口请求地址
     * @param httpMethod   请求类型
     * @param websocket    是不是 wss 协议
     * @return
     * @throws Exception
     */
    private String getAuthURL(String apiKeyByUser, String apiHost, String httpMethod, Boolean websocket) throws Exception {
        String authURL;
        if (apiKeyByUser == null) {
            authURL = URLAuthUtils.getAuthURl(apiHost, sparkConfig.getApiKey(), sparkConfig.getApiSecret(), httpMethod, websocket);
            appid = sparkConfig.getAppid();
        } else {
            // 拆解 879d40fc.fe81b961ccb561c404f844838fa09876.MjUzYTdhMWEyNThiZDBhMTE1NmRjZTk3
            String[] configs = apiKeyByUser.split(".");
            appid = configs[0];
            authURL = URLAuthUtils.getAuthURl(apiHost, configs[1], configs[2], httpMethod, websocket);
        }

        return authURL;
    }

    /**
     * 获取星火对话模型请求参数的 Header
     *
     * @return
     */
    private SparkCompletionRequest.Header getHeader() {
        SparkCompletionRequest.Header header = SparkCompletionRequest.Header.builder()
                .appId(sparkConfig.getAppid())
                .uid(UUID.randomUUID().toString().substring(0, 10))
                .build();
        return header;
    }

    private String base64ToImageUrl(String base64String) {
        // 将 Base64 编码字符串转换为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        // 将字节数组转换为图像URL
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
