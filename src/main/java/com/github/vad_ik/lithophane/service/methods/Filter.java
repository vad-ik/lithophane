package com.github.vad_ik.lithophane.service.methods;


import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Filter {

    /**
     * @param original обрабатываемое изображение
     * @param w
     * @param h        Размер используемого ядра (количество рассматриваемых соседей).
     **/
    public Mat normalizedBlockFilter(Mat original, int w, int h) {//w,h - ширина и высота окна для фильтрации
        Mat out = new Mat();
        Imgproc.blur(original, out, new Size(w, h), new Point(-1, -1));
        return out;
    }

    /**
     * @param original обрабатываемое изображение
     * @param w
     * @param h        Размер используемого ядра (количество рассматриваемых соседей).
     *                 должны   быть нечетными и положительными числами, иначе размер будет рассчитан с использованием sigmaX и sigmaY
     * @param sigmaX   Стандартное отклонение x. подразумевает, что рассчитывается с использованием размера ядра.
     * @param sigmaY   Стандартное отклонение по y. подразумевает, что рассчитывается с использованием размера ядра.
     **/
    public Mat gaussianFilter(Mat original, int w, int h, double sigmaX, double sigmaY) {
        if (w <= 0 || w % 2 != 1 || h <= 0 || h % 2 != 1) {
            log.error("получены некоректные данные для Гауссовского фильтра");
        }
        Mat out = new Mat();
        Imgproc.GaussianBlur(original, out, new Size(w, h), sigmaX, sigmaY);
        return out;
    }

    /**
     * @param original обрабатываемое изображение
     * @param h        ширина и высота окна для фильтрации//должен быть нечетным
     **/
    public Mat medianFilter(Mat original, int h) {
        if (h <= 0 || h % 2 != 1) {
            log.error("получены некоректные данные для медианного фильтра");
        }
        Mat out = new Mat();
        Imgproc.medianBlur(original, out, h);
        return out;
    }

    /**
     * @param original    обрабатываемое изображение
     * @param d           : Диаметр окрестности каждого пикселя.
     * @param sigmaColor: Стандартное отклонение в цветовом пространстве.
     * @param sigmaSpace: Стандартное отклонение в координатном пространстве (в пикселях)
     **/
    public Mat bilateralFilter(Mat original, int d, double sigmaColor, double sigmaSpace) {
        Mat out = new Mat();
        Imgproc.bilateralFilter(original, out, d, sigmaColor, sigmaSpace);
        return out;
    }
}
