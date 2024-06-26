package com.epam.training.gen.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeploymentList {

    private List<Deployment> data;
    private String object;
}