package com.github.vad_ik.lithophane.ui.models.methods;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

public interface Method  {
    public String getName();
    public VerticalLayout getSettings();
    public Mat apply(Mat mat);

    @Component
    public class voidMethod implements Method{

        @Override
        public String getName() {
            return "Void";
        }

        @Override
        public VerticalLayout getSettings() {
            return null;
        }

        @Override
        public Mat apply(Mat mat) {
            return null;
        }
    }
}
