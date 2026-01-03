package com.github.vad_ik.lithophane.service;

import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoadOpenCV {
    public LoadOpenCV() {
        try {
            OpenCV.loadLocally();
//            System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        } catch (Exception e) {
            log.error("OpenCV loading failed: {}", e.getMessage());
            // Здесь можно добавить fallback или сообщение пользователю
        }
    }
}
