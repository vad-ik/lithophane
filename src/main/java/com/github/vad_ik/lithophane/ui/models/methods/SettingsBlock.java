package com.github.vad_ik.lithophane.ui.models.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.ProcessingMethod;
import com.github.vad_ik.lithophane.service.methods.VoidMethod;
import com.github.vad_ik.lithophane.ui.models.SettingsPanel;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsBlock extends VerticalLayout {

    @Getter
    private final ComboBox<ProcessingMethod> comboBox = new ComboBox<>("Метод");
    private final VerticalLayout methodSettings = new VerticalLayout();
    private final SettingsBlockBuilder blockBuilder;
    private final List<ProcessingMethod> methods;
    @Getter
    private ArrayList<MethodsParam> params;
    @Getter
    @Setter
    private SettingsPanel parentPanel;

    public SettingsBlock(SettingsBlockBuilder blockBuilder, List<ProcessingMethod> methods) {
        this.blockBuilder = blockBuilder;
        this.methods = methods.stream()
                .sorted((a, b) -> {
                    if (a instanceof VoidMethod) return -1;
                    if (b instanceof VoidMethod) return 1;

                    int typeCompare = a.getType().compareTo(b.getType());
                    if (typeCompare != 0) return typeCompare;

                    return a.getName().compareTo(b.getName());
                })
                .toList();
        initChangeMethodSettings();
        getStyle()
                .set("border", "2px solid #4a90e2")
                .set("border-radius", "8px")
                .set("padding", "20px");
        log.info("создан блок настроек");
    }

    private void initChangeMethodSettings() {
        comboBox.setItems(methods);
        comboBox.setItemLabelGenerator(ProcessingMethod::getName);
        comboBox.setRenderer(new ComponentRenderer<>(item -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(false);
            layout.setSpacing(false);

            if (!(item instanceof VoidMethod)) {
                Span type = new Span(item.getType().getName());
                type.getStyle()
                        .set("font-size", "11px")
                        .set("color", "var(--lumo-secondary-text-color)");

                Span name = new Span(item.getName());

                layout.add(type, name);
            } else {
                Span name = new Span(item.getName());
                name.getStyle().set("font-weight", "bold");
                layout.add(name);
            }

            return layout;
        }));
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

    public ProcessingMethod getActiveMethod() {
        return comboBox.getValue();
    }
}
