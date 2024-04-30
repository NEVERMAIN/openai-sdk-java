package com.myapp.openai.executor.model.spark.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagesChat {
    /**
     * 指定访问的领域:
     * 注意：不同的取值对应的 url也不一样！
     */
    private String domain = "general";
    /**
     * 图片的宽度
     */
    private Integer width = 512;
    /**
     * 图片的宽度
     */
    private Integer height = 512;
}
