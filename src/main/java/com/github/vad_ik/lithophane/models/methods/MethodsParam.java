package com.github.vad_ik.lithophane.models.methods;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MethodsParam {
    private final Double min;
    private final Double max;
    private final double step;
    private final boolean divisional;
    private final String description;
    private final double defaultValue;

    @Setter
    private double val;

    public MethodsParam(double min, double max, double step, boolean divisional, String description, double defaultValue) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.divisional = divisional;
        this.description = description;
        this.defaultValue = defaultValue;
    }

}
