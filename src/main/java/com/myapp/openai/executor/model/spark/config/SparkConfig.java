package com.myapp.openai.executor.model.spark.config;

import lombok.Data;

/**
 * @description: 讯飞星火模型 配置类
 * 文档: https://console.xfyun.cn/services/bm35
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
public class SparkConfig {

    /**
     * 聊天模型
     */
    private String apiHost = "https://spark-api.xf-yun.com/v3.5/chat";
    /**
     * 生成图片
     */
    private String apiTtiHost = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";
    /**
     * 图片理解
     */
    private String apiPictureHost = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/image";

    private String appid;
    private String apiKey;
    private String apiSecret;

}
