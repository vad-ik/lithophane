package com.github.vad_ik.lithophane.utils;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.min;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

public class VaadinUtils {

    public static IntegerField getIntegerField(Integer min, Integer max, Integer val) {
        IntegerField field = new IntegerField();


        field.setStepButtonsVisible(true);
        if (min != null) field.setMin(min);
        if (max != null) field.setMax(max);
        if (val != null) field.setValue(val);
        field.addValueChangeListener(e -> {
            if (e.isFromClient() && field.isInvalid()) {
                field.setValue(e.getOldValue());
            }
        });

        return field;
    }

    public static NumberField getNumberField(Double min, Double max, Double val) {
        NumberField field = new NumberField();

        field.setStepButtonsVisible(true);
        if (min != null) field.setMin(min);
        if (max != null) field.setMax(max);
        if (val != null) field.setValue(val);
        field.addValueChangeListener(e -> {
            if (e.isFromClient() && field.isInvalid()) {
                field.setValue(e.getOldValue());
            }
        });
        field.setStep(0.1);
        return field;
    }

    }
