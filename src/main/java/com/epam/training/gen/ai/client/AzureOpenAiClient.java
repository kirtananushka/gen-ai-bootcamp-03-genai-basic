package com.epam.training.gen.ai.client;

import com.epam.training.gen.ai.dto.DeploymentList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "azureOpenAiClient", url = "${client-azureopenai-endpoint}")
public interface AzureOpenAiClient {
    @GetMapping("/openai/deployments")
    DeploymentList getDeployments(@RequestHeader("Api-Key") String apiKey);
}
