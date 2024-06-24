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
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SemanticKernelService {
    private final ChatCompletionService.Builder chatCompletionServiceBuilder;
    private final ObjectMapper objectMapper;

    public String getResponseWithSettings(ChatRequest chatRequest) {
        Kernel kernel = buildKernel(chatRequest.getDeploymentName());
        FunctionResult<Object> response = invokePrompt(kernel, chatRequest.getInput(), createPromptExecutionSettings(chatRequest));
        String result = processResponse(response);
        logTokenUsage(response);
        return result;
    }

    public ChatBookResponse getJsonResponseWithSettings(ChatRequest chatRequest) {
        Kernel kernel = buildKernel(chatRequest.getDeploymentName());
        FunctionResult<Object> response = invokePrompt(kernel, createJsonPrompt(chatRequest.getInput()), createPromptExecutionSettings(chatRequest));
        String result = processResponse(response);
        logTokenUsage(response);
        return getChatBookResponse(result);
    }

    private Kernel buildKernel(String deploymentName) {
        ChatCompletionService chatCompletionService = chatCompletionServiceBuilder
                .withModelId(deploymentName)
                .withDeploymentName(deploymentName)
                .build();
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    private FunctionResult<Object> invokePrompt(Kernel kernel, String userInput, PromptExecutionSettings settings) {
        return kernel.invokePromptAsync(userInput)
                .withPromptExecutionSettings(settings)
                .block();
    }

    private String processResponse(FunctionResult<Object> response) {
        String result = Objects.requireNonNull(response.getResult()).toString();
        log.info("Received result: {}", result);
        return result;
    }

    private void logTokenUsage(FunctionResult<Object> response) {
        log.info("Usage: prompt tokens={}, completion tokens={}, total tokens={}",
                Objects.requireNonNull(response.getMetadata().getUsage()).getPromptTokens(),
                response.getMetadata().getUsage().getCompletionTokens(),
                response.getMetadata().getUsage().getTotalTokens());
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
