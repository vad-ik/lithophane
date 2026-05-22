package com.github.vad_ik.lithophane.ui.models.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.ProcessingMethod;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.github.vad_ik.lithophane.utils.VaadinUtils.getIntegerField;
import static com.github.vad_ik.lithophane.utils.VaadinUtils.getNumberField;

@Service
public class SettingsBlockBuilder {

    public VerticalLayout build(ProcessingMethod method, ArrayList<MethodsParam> params) {

        VerticalLayout layout = new VerticalLayout();
        for (MethodsParam param : params) {
            layout.add(new Span(param.getDescription()));
            if (param.isDivisional()) {
                layout.add(getNumberField(param));
            } else {
                layout.add(getIntegerField(param));
            }
        }
        return layout;
    }
}