package com.github.vad_ik.lithophane.ui.models.methods;

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

import java.util.List;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsBlock extends VerticalLayout {

    private final ComboBox<Method> comboBox = new ComboBox<>("Метод");
    private final VerticalLayout methodSettings = new VerticalLayout();

    private final List<Method> methods;
    @Getter
    @Setter
    private SettingsPanel parentPanel;

    public SettingsBlock(List<Method> methods) {
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
        comboBox.setItemLabelGenerator(Method::getName);
        add(comboBox);
        add(methodSettings);
        comboBox.addValueChangeListener(e -> {

            log.info("выбран метод {}",e.getValue().getName());
            if (e.getValue().getClass().equals(Method.voidMethod.class)){
                parentPanel.delBlock(this);
                return;
            }else {
                parentPanel.update();
            }
            methodSettings.removeAll();
            methodSettings.add(e.getValue().getSettings());
        });
    }
    public Mat apply(Mat activeImage){
        if (comboBox.getValue()!=null) {
            return comboBox.getValue().apply(activeImage);
        }
        return null;
    }
    public Method getActiveMethod(){
        return comboBox.getValue();
    }
}
