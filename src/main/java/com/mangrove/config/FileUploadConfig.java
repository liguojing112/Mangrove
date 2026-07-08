package com.mangrove.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file")
public class FileUploadConfig {

    private String uploadDir = "./uploads";
}
