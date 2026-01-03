package com.github.vad_ik.lithophane.ui.models.methods.simpleMethod;

import com.github.vad_ik.lithophane.service.methods.KMeans;
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
public class Clustering implements Method {

    private final KMeans kMeans;
    private final IntegerField numOfKlastersField;

    public Clustering(KMeans kMeans) {
        this.kMeans = kMeans;
        numOfKlastersField= getIntegerField(0,null,5);
    }

    @Override
    public String getName() {
        return "Кластеризация";
    }

    @Override
    public VerticalLayout getSettings() {
        VerticalLayout VerticalLayout=new VerticalLayout();
        VerticalLayout.add(new Span("Количество кластеров"));
        VerticalLayout.add(numOfKlastersField);
        return VerticalLayout;
    }

    @Override
    public Mat apply(Mat mat) {
        return kMeans.applyKMeansToImage(mat,numOfKlastersField.getValue());
    }
}
