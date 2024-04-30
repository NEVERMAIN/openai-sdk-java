package com.myapp.openai.executor.model.chatglm.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 智谱AI配置类
 * 文档: https://open.bigmodel.cn/dev/api#glm-3-turbo
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMConfig {

    @Getter
    @Setter
    private String apiHost = "https://open.bigmodel.cn/";

    @Getter
    private String apiKey;
    @Getter
    private String apiSecret;

    /**
     * 智谱Ai apiSecretKey = {apiKey}.{apiSecret}
     * 申请网址: https://open.bigmodel.cn/usercenter/apikeys
     */
    @Setter
    private String apiSecretKey;

    @Getter
    private String V4_COMPLETIONS = "api/paas/v4/chat/completions";

    @Getter
    private String V3_MODEL_SSE_INVOKE = "api/paas/v3/model-api/charglm-3/sse-invoke";

    @Getter
    private String v4_images = "api/paas/v4/images/generations";

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
        if (StringUtils.isBlank(apiSecretKey)) {
            log.warn("not apiSecretKey set");
        } else {
            String[] arrStr = apiSecretKey.split("\\.");
            if (arrStr.length != 2) {
                throw new RuntimeException("invalid apiSecretKey");
            }
            this.apiKey = arrStr[0];
            this.apiSecret = arrStr[1];
        }
    }



}
