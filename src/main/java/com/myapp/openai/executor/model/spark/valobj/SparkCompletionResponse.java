package com.myapp.openai.executor.model.spark.valobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SparkCompletionResponse {

    private Header header;
    private Payload payload;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Header {
        /**
         * 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解
         */
        private Integer code;
        /**
         * 会话是否成功的描述信息
         */
        private String message;
        /**
         * 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
         */
        private String sid;
        /**
         * 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
         */
        private Integer status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Payload {
        private Choices choices;
        private Usage usage;

    }

    /**
     * 错误码，0表示正常，非 0表示出错；
     *
     */
    @Getter
    public enum Code{
        /**
         * 正常
         */
        SUCCCESS(0)
        ;
        Code(int value){this.value = value;}
        private final int value;

    }

    @Getter
    public enum Status{
        /**
         * 开始
         */
        START(0),
        ING(1),
        END(2),

        ;
        Status(int value){this.value = value;}
        private final int value;
    }

}
