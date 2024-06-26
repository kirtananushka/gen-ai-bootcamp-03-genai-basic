package com.epam.training.gen.ai.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
public class DeploymentConfig {

    @Value("#{'${client-azureopenai-valid-deployment-names}'.split(',')}")
    private List<String> validDeploymentNamesList;
}
