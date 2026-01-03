package com.github.vad_ik.lithophane.ui;

import com.github.vad_ik.lithophane.ui.controller.ImageController;
import com.github.vad_ik.lithophane.ui.models.SettingsPanel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Route("")
@Slf4j
public class MainPage extends HorizontalLayout {

    public MainPage(SettingsPanel settingsPanel, ImageController imageController) {
        log.info("открыта страница");
        settingsPanel.initSettingsPanel(imageController);
        add(settingsPanel, imageController.getOriginalImage(), imageController.getPrepareImage());
        setSizeFull();
    }
}