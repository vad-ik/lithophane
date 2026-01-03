package com.github.vad_ik.lithophane.utils;

import com.vaadin.flow.component.html.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Base64;

public class MatUtils {
    public static Mat convertPngBytesToMat(byte[] imageData) {
        // Создаем MatOfByte из byte[]
        MatOfByte matOfByte = new MatOfByte(imageData);

        // Декодируем в Mat с помощью Imgcodecs
        Mat mat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);

        // Освобождаем промежуточный объект
        matOfByte.release();

        return mat;
    }

    public static String convertMatToVaadinImage(Mat mat) {
        // 1. Конвертируем Mat в PNG byte[]
        byte[] imageBytes = convertMatToPngBytes(mat);

        // 2. Конвертируем byte[] в Base64 строку
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // 3. Создаем Vaadin Image с Data URI
        return "data:image/png;base64," + base64Image;
    }


    private static byte[] convertMatToPngBytes(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();

        boolean success = Imgcodecs.imencode(".png", mat, matOfByte);

        if (!success) {
            throw new RuntimeException("Не удалось закодировать изображение в PNG");
        }
        byte[] byteArray = matOfByte.toArray();
        matOfByte.release();

        return byteArray;
    }
}
