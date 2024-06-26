package com.epam.training.gen.ai.validator;

import com.epam.training.gen.ai.config.DeploymentConfig;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeploymentNameValidator implements ConstraintValidator<ValidDeploymentName, String> {

    private final DeploymentConfig deploymentConfig;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return deploymentConfig.getValidDeploymentNamesList().contains(value);
    }
}
