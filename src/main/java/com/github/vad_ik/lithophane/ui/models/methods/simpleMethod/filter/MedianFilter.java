package com.github.vad_ik.lithophane.ui.models.methods.simpleMethod.filter;

import com.github.vad_ik.lithophane.service.methods.Filter;
import com.github.vad_ik.lithophane.ui.models.methods.Method;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import org.opencv.core.Mat;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.github.vad_ik.lithophane.utils.VaadinUtils.getIntegerField;
import static com.github.vad_ik.lithophane.utils.VaadinUtils.getNumberField;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MedianFilter implements Method {
    private final Filter filter;
    private final IntegerField h;


    public MedianFilter(Filter filter) {
        this.filter = filter;
        h = getIntegerField(1, 1000, 5);
    }

    @Override
    public String getName() {
        return "Медианный фильтр";
    }

    @Override
    public VerticalLayout getSettings() {
        VerticalLayout VerticalLayout = new VerticalLayout();

        VerticalLayout.add(new Span("Ширина окна"));
        VerticalLayout.add(h);
        return VerticalLayout;
    }

    @Override
    public Mat apply(Mat mat) {
        return filter.medianFilter(mat,  h.getValue());
    }
}
