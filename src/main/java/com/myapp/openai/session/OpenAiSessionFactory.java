package com.myapp.openai.session;

/**
 * @description: 会话工厂
 * @author: 云奇迹
 * @date: 2024/4/5
 */
public interface OpenAiSessionFactory {

    /**
     * 开启一个会话
     * @return 会话
     */
    OpenAiSession openSession();
}
