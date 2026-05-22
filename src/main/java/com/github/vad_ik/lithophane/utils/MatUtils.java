package com.github.vad_ik.lithophane.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class MatUtils {
    public static Mat convertPngBytesToMat(byte[] imageData) {
        MatOfByte matOfByte = new MatOfByte(imageData);
        Mat mat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);
        matOfByte.release();

        return mat;
    }

    public static String convertMatToVaadinImage(Mat mat) {
        byte[] imageBytes = convertMatToPngBytes(mat);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
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

    public static BufferedImage convertPngBytesToBufferedImage(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(bis);
            bis.close();
            return bufferedImage;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось декодировать PNG байты в BufferedImage", e);
        }
    }

    public static String convertBufferedImageToVaadinImage(BufferedImage bufferedImage) {
        byte[] imageBytes = convertBufferedImageToPngBytes(bufferedImage);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64Image;
    }

    private static byte[] convertBufferedImageToPngBytes(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] byteArray = baos.toByteArray();
            baos.close();
            return byteArray;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось закодировать BufferedImage в PNG", e);
        }
    }

    public static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        return mat;
    }
}
