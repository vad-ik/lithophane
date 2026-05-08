package com.github.vad_ik.lithophane.utils;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

public class VaadinUtils {

    public static IntegerField getIntegerField(MethodsParam param) {
        IntegerField field = new IntegerField();


        field.setStepButtonsVisible(true);
        if (param.getMin() != null) field.setMin((int) (double) param.getMin());
        if (param.getMax() != null) field.setMax((int) (double) param.getMax());
        field.setStep((int) param.getStep());
        field.setValue((int) param.getDefaultValue());
        param.setVal((int) param.getDefaultValue());
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
        field.setValue(param.getDefaultValue());
        param.setVal(param.getDefaultValue());
        field.setStep(param.getStep());
        field.addValueChangeListener(e -> {
            if (e.isFromClient() && field.isInvalid()) {
                field.setValue(e.getOldValue());
            }
            param.setVal(field.getValue());
        });

        return field;
    }
}
