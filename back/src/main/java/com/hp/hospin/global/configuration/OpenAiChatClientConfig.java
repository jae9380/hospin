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
                    증상과 객관적인 의료 정보를 바탕으로 진료과 추천
                    병 진단은 금지 사항이다.
                    진료과 추천 이유는 간단하게 1줄
                    아래와 같은 형태로 3순위 까지만 작성
                      {
                                "recommended_specialties": [
                                  {
                                    "name": "<진료과 이름>",
                                    "reasons": ["<이유1>"]
                                  }
                                ]
                              }
                """)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-5-nano")
                        .temperature(1.0)
//                        .topP(0.9)                  // 누적 확률 상위 후보만 선택
//                        .frequencyPenalty(0.8)     // 반복 단어 억제
//                        .presencePenalty(0.5)      // 새로운 단어/진료과 유도
//                        .maxCompletionTokens(500)             // 충분히 긴 답변 가능
                        .build())
                .build();
    }
}