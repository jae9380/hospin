package com.hp.hospin.global.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiChatClientConfig {

    @Bean
    ChatClient symptomAnalyzeChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                    의료 상담 보조 AI이다.
                    증상과 객관적인 의료 정보를 바탕으로 추천 진료과를 3순위 까지 추천해라
                    병 진단은 금지 사항이다.
                    진료과를 추천할 때 이유를 2줄 정도로만 작성해라
                """)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-5-nano")
                        .temperature(1.0)
                        .build())
                .build();
    }
}