package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.dto.BookDto;
import com.epam.training.gen.ai.dto.ChatBookResponse;
import com.epam.training.gen.ai.dto.ChatRequest;
import com.epam.training.gen.ai.exception.OpenAiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SemanticKernelService {
    private final Kernel kernel;
    private final ObjectMapper objectMapper;

    public ChatBookResponse getJsonResponseWithSettings(ChatRequest chatRequest) {
        log.info("Entering getJsonResponseWithSettings with input: {}", chatRequest.getInput());
        String userInput = chatRequest.getInput();
        PromptExecutionSettings settings = createPromptExecutionSettings(chatRequest);

        FunctionResult<Object> response = kernel.invokePromptAsync(createJsonPrompt(userInput))
                .withPromptExecutionSettings(settings)
                .block();

        String result = response.getResult().toString();
        log.info("Received result: {}", result);

        log.info("Usage: prompt tokens={}, completion tokens={}, total tokens={}",
                response.getMetadata().getUsage().getPromptTokens(),
                response.getMetadata().getUsage().getCompletionTokens(),
                response.getMetadata().getUsage().getTotalTokens());

        return getChatBookResponse(result);
    }

    private String createJsonPrompt(String input) {
        return """
                ## Instructions
                Provide the response using the following format:
                [
                    {
                        "author": "author1",
                        "title": "title1"
                    },
                    {
                        "author": "author2",
                        "title": "title2"
                    }
                ]
                ```
                ## User Input
                The user input is: "%s"
                }
                ```
                """.formatted(input);
    }

    private PromptExecutionSettings createPromptExecutionSettings(ChatRequest chatRequest) {
        return PromptExecutionSettings.builder()
                .withMaxTokens(chatRequest.getMaxTokens() != null ? chatRequest.getMaxTokens() : 256)
                .withTemperature(chatRequest.getTemperature() != null ? chatRequest.getTemperature() : 1.0)
                .build();
    }

    private ChatBookResponse getChatBookResponse(String result) {
        try {
            List<BookDto> jsonResult = mapJsonToBooks(result).getResponse();
            log.info("Mapped result: {}", jsonResult);
            ChatBookResponse chatBookResponse = mapJsonToBooks(result);
            log.info("Exiting getJsonResponseWithSettings with response: {}", chatBookResponse);
            return chatBookResponse;
        } catch (Exception e) {
            log.error("Error while mapping JSON to books", e);
            throw new OpenAiException("Error mapping JSON to books: " + e.getMessage() + ".\n\nOriginal result: " + result, e);
        }
    }

    private ChatBookResponse mapJsonToBooks(String json) throws IOException {
        List<BookDto> books = objectMapper.readValue(json, new TypeReference<>() {
        });
        return new ChatBookResponse(books);
    }
}
