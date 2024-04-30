package com.myapp.openai.executor.model.chatglm.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: ChatGLM 对话角色
 * @author: 云奇迹
 * @date: 2024/4/5
 */
@Getter
@AllArgsConstructor
public enum Role {
    /**
     * user 用户输入的内容，role位user
     */
    user("user"),
    /**
     * 模型生成的内容，role 位 assistant
     */
    assistant("assistant"),
    ;
    private final String code;

    /**
     * 获取枚举值
     * @param code
     * @return
     */
    public static Role get(String code) {
        Role[] values = Role.values();
        for (Role value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
