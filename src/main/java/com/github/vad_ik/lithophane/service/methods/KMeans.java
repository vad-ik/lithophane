package com.github.vad_ik.lithophane.service.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.github.vad_ik.lithophane.utils.ExceptionUtils.throwIfNumberOfParametersIsNotEqual;

@Slf4j
@Service
public class KMeans implements MethodsGen {

    @Override
    public String getName() {
        return "Метод k-средних (k-means clustering)";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 1000, 1, false, "Количество кластеров", 5));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {


        Mat floatImg = new Mat();
        Mat labels = new Mat();
        Mat centers = new Mat();
        Mat data = null;
        try {
            throwIfNumberOfParametersIsNotEqual(params, 1, getName());
            log.info("началась кластеризация KMeans");
            img.convertTo(floatImg, CvType.CV_32F);

            data = floatImg.reshape(
                    floatImg.channels(),
                    floatImg.rows() * floatImg.cols()
            );


            Core.kmeans(
                    data,
                    (int) params.getFirst().getVal(),
                    labels,
                    new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 100, 0.1),
                    10,
                    Core.KMEANS_PP_CENTERS,
                    centers
            );

            centers.convertTo(centers, CvType.CV_8U);
            Mat result = new Mat(img.size(), CvType.CV_8UC3);

            int[] labelBuf = new int[1];
            int index = 0;
            for (int y = 0; y < img.rows(); y++) {
                for (int x = 0; x < img.cols(); x++) {

                    labels.get(index++, 0, labelBuf);
                    int label = labelBuf[0];

                    double b = centers.get(label, 0)[0];
                    double g = centers.get(label, 1)[0];
                    double r = centers.get(label, 2)[0];

                    result.put(y, x, b, g, r);
                }
            }

            log.info("кластеризация KMeans закончилась");
            return result;
        } finally {
            // 7. Освобождение ВСЕХ временных матриц, КРОМЕ result
            floatImg.release();
            labels.release();
            centers.release();
            img.release();
            if (data != null) {
                data.release();
            }
        }
    }
}
