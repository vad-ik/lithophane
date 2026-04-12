package com.github.vad_ik.lithophane.ui.models.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.service.methods.VoidMethod;
import com.github.vad_ik.lithophane.ui.models.SettingsPanel;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsBlock extends VerticalLayout {

    private final ComboBox<MethodsGen> comboBox = new ComboBox<>("Метод");
    private final VerticalLayout methodSettings = new VerticalLayout();
    private final SettingsBlockBuilder blockBuilder;
    private ArrayList<MethodsParam> params;

    private final List<MethodsGen> methods;
    @Getter
    @Setter
    private SettingsPanel parentPanel;

    public SettingsBlock(SettingsBlockBuilder blockBuilder, List<MethodsGen> methods) {
        this.blockBuilder = blockBuilder;
        this.methods = methods;
        initChangeMethodSettings();
        getStyle()
                .set("border", "2px solid #4a90e2")
                .set("border-radius", "8px")
                .set("padding", "20px");
        log.info("создан блок настроек");
    }

    private void initChangeMethodSettings() {
        comboBox.setItems(methods);
        comboBox.setItemLabelGenerator(MethodsGen::getName);
        add(comboBox);
        add(methodSettings);
        comboBox.addValueChangeListener(e -> {

            log.info("выбран метод {}", e.getValue().getName());
            if (e.getValue().getClass().equals(VoidMethod.class)) {
                parentPanel.delBlock(this);
                return;
            } else {
                parentPanel.update();
            }
            methodSettings.removeAll();
            params = e.getValue().getParams();
            methodSettings.add(blockBuilder.build(e.getValue(), params));
        });
    }

    public Mat apply(Mat activeImage) {
        if (comboBox.getValue() != null) {
            Long time =System.currentTimeMillis();
            log.info("метод {} начал работу", getActiveMethod().getName());
            Mat ans= comboBox.getValue().apply(activeImage, params);
            log.info("метод {} закончил работу за {} с", getActiveMethod().getName(),(System.currentTimeMillis()-time)/1000.0);
            return ans;
        }
        log.error("метод {} вернул нулевой результат",getActiveMethod().getName());
        throw new RuntimeException("Неверный блок настроек");

    }

    public MethodsGen getActiveMethod() {
        return comboBox.getValue();
    }
}
