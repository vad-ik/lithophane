package com.github.vad_ik.lithophane.ui.controller;

import com.vaadin.flow.component.html.Image;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImageController {

    private final Image originalImage = createImage();
    private final Image prepareImage = createImage();

    public void setOriginalImage(String dataUri) {
        setImage(originalImage, dataUri);
    }

    public void setPrepareImage(String dataUri) {
        setImage(prepareImage, dataUri);
    }

    private void setImage(Image image, String dataUri) {
        image.setSrc(dataUri);
    }

    private Image createImage() {
        return new Image("./image/void.jpg", "что то пошло не так");
    }
}
