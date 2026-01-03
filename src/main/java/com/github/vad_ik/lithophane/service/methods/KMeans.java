package com.github.vad_ik.lithophane.service.methods;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KMeans {

    public Mat applyKMeansToImage(Mat img, int k) {
        log.info("началась кластеризация KMeans");
        Mat floatImg = new Mat();
        img.convertTo(floatImg, CvType.CV_32F);

        Mat data = floatImg.reshape(
                floatImg.channels(),
                floatImg.rows() * floatImg.cols()
        );

        Mat labels = new Mat();
        Mat centers = new Mat();

        Core.kmeans(
                data,
                k,
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
    }
}
