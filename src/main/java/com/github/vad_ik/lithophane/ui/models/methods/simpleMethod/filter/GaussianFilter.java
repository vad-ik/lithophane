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
public class GaussianFilter implements Method {
    private final Filter filter;
    private final IntegerField w;
    private final IntegerField h;

    private final NumberField sigmaX;
    private final NumberField sigmaY;

    public GaussianFilter(Filter filter) {
        this.filter = filter;
        w = getIntegerField(1, 1001, 5);
        w.setStep(2);
        h = getIntegerField(1, 1001, 5);
        h.setStep(2);

        sigmaX = getNumberField(0.0, 1000.0, 0.0);
        sigmaY = getNumberField(0.0, 1000.0, 0.0);
    }

    @Override
    public String getName() {
        return "Гаусовский фильтр";
    }

    @Override
    public VerticalLayout getSettings() {
        VerticalLayout VerticalLayout = new VerticalLayout();
        VerticalLayout.add(new Span("Высота окна"));
        VerticalLayout.add(h);
        VerticalLayout.add(new Span("Ширина окна"));
        VerticalLayout.add(w);
        VerticalLayout.add(new Span("Стандартное отклонение по x"));
        VerticalLayout.add(sigmaX);
        VerticalLayout.add(new Span("Стандартное отклонение по y"));
        VerticalLayout.add(sigmaY);
        return VerticalLayout;
    }

    @Override
    public Mat apply(Mat mat) {
        return filter.gaussianFilter(mat, w.getValue(), h.getValue(), sigmaX.getValue(), sigmaY.getValue());
    }
}
