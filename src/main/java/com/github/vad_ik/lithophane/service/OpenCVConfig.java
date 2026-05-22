package com.github.vad_ik.lithophane.service;

import jakarta.annotation.PostConstruct;
import nu.pattern.OpenCV;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCVConfig {

    @PostConstruct
    public void init() {
        OpenCV.loadLocally();
    }
}