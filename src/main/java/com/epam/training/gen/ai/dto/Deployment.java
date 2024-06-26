package com.epam.training.gen.ai.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Deployment {
    private String id;
    private String model;
    private String owner;
    private String object;
    private String status;
    private long created_at;
    private long updated_at;
    private ScaleSettings scale_settings;
    private Features features;

    @Data
    public static class Features {
        private boolean rate;
        private boolean tokenize;
        private boolean truncate_prompt;
        private boolean configuration;
        private boolean system_prompt;
        private boolean tools;
        private boolean seed;
        private boolean url_attachments;
        private boolean folder_attachments;
    }

    @Data
    public static class ScaleSettings {
        private String scale_type;
    }
}
