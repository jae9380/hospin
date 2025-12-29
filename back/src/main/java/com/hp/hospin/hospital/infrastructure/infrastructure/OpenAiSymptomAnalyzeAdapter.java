package com.hp.hospin.hospital.infrastructure.infrastructure;

import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiSymptomAnalyzeAdapter implements SymptomAnalyzePort {
    private final OpenAiChatModel openAiChatModel;

    @Override
    public String analyze(String text) {

        SystemMessage systemMessage = new SystemMessage("");
        UserMessage userMessage = new UserMessage(text);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("gpt-5-nano")
                .temperature(0.3)
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        ChatResponse response = openAiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
