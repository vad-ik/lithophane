package com.github.vad_ik.lithophane.service.methods.segmentations;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class MeanShiftProcessor implements MethodsGen {

    @Override
    public String getName() {
        return "Метод MeanShiftProcessor";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 500, 1, true, "Радиус области (в пикселях), в пределах которой алгоритм ищет похожие пиксели", 20));
        params.add(new MethodsParam(0, 500, 1, true, "Насколько сильно алгоритм допускает различие цветов при объединении пикселей.", 30));
        params.add(new MethodsParam(0, 2, 1, false, "уровни сглаживания", 1));
        return params;
    }

    @Override
    public Type getType() {
        return Type.segmentations;
    }


    public Mat apply(Mat src, ArrayList<MethodsParam> params) {
        if (src == null || src.empty()) {
            throw new IllegalArgumentException("Input image is empty");
        }

        if (params == null || params.size() < 2) {
            throw new IllegalArgumentException("Not enough parameters");
        }

        // Параметры
        double sp = params.get(0).getVal(); // spatial radius
        double sr = params.get(1).getVal(); // color radius
        int maxLevel = (int) params.get(2).getVal() ;

        Mat dst = new Mat();

        // ВАЖНО: pyrMeanShiftFiltering ожидает 8-bit 3-channel (BGR)
        Mat input = new Mat();
        if (src.type() != org.opencv.core.CvType.CV_8UC3) {
            src.convertTo(input, org.opencv.core.CvType.CV_8UC3);
        } else {
            input = src.clone();
        }

        Imgproc.pyrMeanShiftFiltering(
                input,
                dst,
                sp,        // spatial window radius
                sr,        // color window radius
                maxLevel   // pyramid levels
        );

        return dst;
    }
}