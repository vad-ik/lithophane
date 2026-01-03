package com.github.vad_ik.lithophane.utils;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.min;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

public class VaadinUtils {

    public static IntegerField getIntegerField(MethodsParam param) {
        IntegerField field = new IntegerField();


        field.setStepButtonsVisible(true);
        if (param.getMin() != null) field.setMin((int)(double)param.getMin());
        if (param.getMax() != null) field.setMax((int)(double)param.getMax());
        field.setValue((int) param.getDefaultValue());
        field.setStep((int) param.getStep());
        field.addValueChangeListener(e -> {
            if (e.isFromClient() && field.isInvalid()) {
                field.setValue(e.getOldValue());
            }
            param.setVal(field.getValue());
        });

        return field;
    }
    public static NumberField getNumberField(MethodsParam param) {
        NumberField field = new NumberField();


        field.setStepButtonsVisible(true);
        if (param.getMin() != null) field.setMin(param.getMin());
        if (param.getMax() != null) field.setMax(param.getMax());
        field.setValue( param.getDefaultValue());
        field.setStep( param.getStep());
        field.addValueChangeListener(e -> {
            if (e.isFromClient() && field.isInvalid()) {
                field.setValue(e.getOldValue());
            }
            param.setVal(field.getValue());
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
