package com.epam.training.gen.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DeploymentConfig {

    @Value("${client-azureopenai-valid-deployment-names}")
    private String validDeploymentNames;

    @Getter
    private List<String> validDeploymentNamesList;

    @PostConstruct
    public void init() {
        validDeploymentNamesList = Arrays.asList(validDeploymentNames.split(","));
    }
}
