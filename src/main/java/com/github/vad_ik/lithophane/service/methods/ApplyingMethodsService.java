package com.github.vad_ik.lithophane.service.methods;

import com.github.vad_ik.lithophane.models.methods.PipelineStep;
import com.github.vad_ik.lithophane.utils.MatUtils;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.github.vad_ik.lithophane.utils.MatUtils.convertBufferedImageToMat;

@Slf4j
@Service
public class ApplyingMethodsService {

    public String apply(BufferedImage bufferedImage, List<PipelineStep> methods) {
        Mat activeImage = convertBufferedImageToMat(bufferedImage);
        for (PipelineStep method : methods) {
            Mat prev = activeImage;
            var activeMethod = method.method();
            long time = System.currentTimeMillis();
            log.info("метод {} начал работу", activeMethod.getName());
            activeImage = activeMethod.apply(activeImage, method.param());
            log.info("метод {} закончил работу за {} с", activeMethod.getName(), (System.currentTimeMillis() - time) / 1000.0);
            if (prev != activeImage) {
                prev.release();
            }
        }
        String img = MatUtils.convertMatToVaadinImage(activeImage);
        activeImage.release();
        return img;
    }
}
