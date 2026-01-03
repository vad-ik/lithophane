package com.github.vad_ik.lithophane.ui.models;

import com.github.vad_ik.lithophane.ui.controller.ImageController;
import com.github.vad_ik.lithophane.ui.models.methods.SettingsBlock;
import com.github.vad_ik.lithophane.utils.MatUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsPanel extends VerticalLayout {
    private ImageController imageController;
    private final ArrayList<SettingsBlock> settingsBlocks = new ArrayList<>();
    private final VerticalLayout methods = new VerticalLayout();
    @Autowired
    private ObjectProvider<SettingsBlock> settingsBlockProvider;

    private Mat activeImage;

    public void initSettingsPanel(ImageController imageController) {

        this.imageController = imageController;
        initUploadButton();
        addSettingsBlock();
        initActiveButton();
        activeImage = Imgcodecs.imread("src/main/resources/static/image/void.jpg");
    }

    private void initUploadButton() {
        InMemoryUploadHandler inMemoryHandler = UploadHandler
                .inMemory((metadata, data) -> {

                    activeImage = MatUtils.convertPngBytesToMat(data);
                    imageController.setOriginalImage(MatUtils.convertMatToVaadinImage(activeImage));
                    log.info("загружено новое фото");
                });
        Upload upload = new Upload(inMemoryHandler);
        upload.setMaxFiles(1);

        upload.setAcceptedFileTypes(".png", ".jpg");

        Button uploadButton = new Button("Выберите изображение");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        upload.setUploadButton(uploadButton);
        add(upload);

    }


    private void initActiveButton() {
        Button active = new Button("Применить");
        active.addClickListener(_ -> {
            Mat newImage = activeImage.clone();
            for (SettingsBlock settingsBlock : settingsBlocks) {
                if (settingsBlock.getParentPanel() == null || settingsBlock.getActiveMethod()==null) {
                    continue;
                }
                Mat tmp = settingsBlock.apply(newImage);
                if (tmp != null) {
                    newImage = tmp;
                } else {
                    log.error("метод {} вернул нулевой результат",settingsBlock.getActiveMethod().getName());
                    throw new RuntimeException("Неверный блок настроек");
                }
            }

            imageController.setPrepareImage(MatUtils.convertMatToVaadinImage(newImage));
        });
        add(active);
    }

    public void delBlock(SettingsBlock block) {
        settingsBlocks.remove(block);
        update();
    }

    public void addSettingsBlock() {
        settingsBlocks.add(createSettingsBlock());
        update();
        add(methods);
    }

    public void update() {

        if (settingsBlocks.isEmpty() || settingsBlocks.getLast().getActiveMethod() != null) {
            settingsBlocks.add(createSettingsBlock());
        }
        methods.removeAll();
        for (SettingsBlock settingsBlock : settingsBlocks) {
            methods.add(settingsBlock);
        }
    }

    private SettingsBlock createSettingsBlock() {
        SettingsBlock block = settingsBlockProvider.getObject();
        block.setParentPanel(this);
        return block;
    }
}
