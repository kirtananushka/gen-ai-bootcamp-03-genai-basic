package com.epam.training.gen.ai.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DeploymentNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDeploymentName {
    String message() default "Invalid deployment name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
