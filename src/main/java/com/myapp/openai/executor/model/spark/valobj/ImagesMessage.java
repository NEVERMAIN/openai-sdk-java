package com.myapp.openai.executor.model.spark.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagesMessage {
    private List<ImagesText> text;
}