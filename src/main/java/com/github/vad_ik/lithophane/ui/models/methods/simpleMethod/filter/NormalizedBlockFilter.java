package com.github.vad_ik.lithophane.ui.models.methods.simpleMethod.filter;

import com.github.vad_ik.lithophane.service.methods.Filter;
import com.github.vad_ik.lithophane.ui.models.methods.Method;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import org.opencv.core.Mat;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.github.vad_ik.lithophane.utils.VaadinUtils.getIntegerField;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NormalizedBlockFilter implements Method {
private final Filter filter;
    private final IntegerField w;
    private final IntegerField h;

    public NormalizedBlockFilter(Filter filter) {
        this.filter = filter;
        w=getIntegerField(0,1000,5);
        h=getIntegerField(0,1000,5);
    }

    @Override
    public String getName() {
        return "Нормализованный блочный фильтр";
    }

    @Override
    public VerticalLayout getSettings() {
        VerticalLayout VerticalLayout=new VerticalLayout();
        VerticalLayout.add(new Span("Высота окна"));
        VerticalLayout.add(h);
        VerticalLayout.add(new Span("Ширина окна"));
        VerticalLayout.add(w);
        return VerticalLayout;
    }

    @Override
    public Mat apply(Mat mat) {
        return filter.normalizedBlockFilter(mat, w.getValue(), h.getValue());
    }
}
