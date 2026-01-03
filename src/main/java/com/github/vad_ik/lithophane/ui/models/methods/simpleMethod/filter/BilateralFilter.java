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
public class BilateralFilter implements Method {
    private final Filter filter;
    private final IntegerField h;

    private final NumberField sigmaColor;
    private final NumberField sigmaSpace;

    public BilateralFilter(Filter filter) {
        this.filter = filter;
        h = getIntegerField(1, 1000, 5);
        h.setStep(2);

        sigmaColor = getNumberField(0.0, 1000.0, 0.0);
        sigmaSpace = getNumberField(0.0, 1000.0, 0.0);
    }

    @Override
    public String getName() {
        return "Двусторонний фильтр";
    }

    @Override
    public VerticalLayout getSettings() {
        VerticalLayout VerticalLayout = new VerticalLayout();
        VerticalLayout.add(new Span("Диаметр  окна (нечетное)"));
        VerticalLayout.add(h);
        VerticalLayout.add(new Span("Отклонение в цветовом пространстве (небольшие значения ≈ 10 сохраняют границы, большие значения ≈ 100 размывают объекты)"));
        VerticalLayout.add(sigmaColor);
        VerticalLayout.add(new Span("Отклонение в координатном пространстве (больше значения имеет ближайшие пиксели)"));
        VerticalLayout.add(sigmaSpace);
        return VerticalLayout;
    }

    @Override
    public Mat apply(Mat mat) {
        return filter.bilateralFilter(mat,  h.getValue(), sigmaColor.getValue(), sigmaSpace.getValue());
    }
}
