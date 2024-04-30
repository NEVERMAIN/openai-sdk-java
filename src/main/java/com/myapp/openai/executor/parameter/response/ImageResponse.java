package com.myapp.openai.executor.parameter.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse implements Serializable {

    /**
     * 请求创建时间，是以秒为单位的Unix时间戳。
     */
    private Long created;
    /**
     * 数组，包含生成的图片 URL。目前数组中只包含一张图片。
     */
    private List<Item> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item{
        /**
         * 图片链接
         */
        private String url;
    }


}
