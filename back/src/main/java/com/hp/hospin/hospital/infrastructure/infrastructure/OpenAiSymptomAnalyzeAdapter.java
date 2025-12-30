package com.hp.hospin.hospital.infrastructure.infrastructure;

import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiSymptomAnalyzeAdapter implements SymptomAnalyzePort {
    private final ChatClient symptomAnalyzeChatClient;

    @Override
    public String analyze(String text) {
        return symptomAnalyzeChatClient.prompt()
                .user(text)
                .call()
                .content();
    }
}
