package com.github.vad_ik.lithophane.service.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.vad_ik.lithophane.utils.ExceptionUtils.throwIfNumberOfParametersIsNotEqual;

@Slf4j
@Service
public class KMeansNormal implements MethodsGen {

    @Override
    public String getName() {
        return "Метод k-средних (с нормализацией)(k-means clustering)";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 1000, 1, false, "Количество кластеров", 5));
        params.add(new MethodsParam(0, 1000, 1, false, "нормализация (меньше - сильнее)", 5));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {
        int k = (int) params.get(0).getVal();
        int maxPerColor = (int) params.get(1).getVal();

        return cluster (img,k,maxPerColor);

    }



        public static Mat cluster(Mat img, int k, int maxPerColor) {

            Mat lab = new Mat();
            Imgproc.cvtColor(img, lab, Imgproc.COLOR_BGR2Lab);

            int rows = lab.rows();
            int cols = lab.cols();

            Map<Integer, Integer> colorCount = new HashMap<>();
            List<float[]> samples = new ArrayList<>();

            // --- 1. собираем сбалансированные сэмплы ---
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {

                    double[] pixel = lab.get(y, x);

                    int l = (int) pixel[0] >> 3; // квантование
                    int a = (int) pixel[1] >> 3;
                    int b = (int) pixel[2] >> 3;

                    int key = (l << 16) | (a << 8) | b;

                    int count = colorCount.getOrDefault(key, 0);

                    if (count < maxPerColor) {
                        samples.add(new float[]{
                                (float) pixel[0],
                                (float) pixel[1],
                                (float) pixel[2]
                        });
                        colorCount.put(key, count + 1);
                    }
                }
            }

            // --- 2. превращаем в Mat ---
            Mat data = new Mat(samples.size(), 3, CvType.CV_32F);
            for (int i = 0; i < samples.size(); i++) {
                data.put(i, 0, samples.get(i));
            }

            // --- 3. kmeans ---
            Mat labels = new Mat();
            Mat centers = new Mat();

            Core.kmeans(
                    data,
                    k,
                    labels,
                    new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 20, 1.0),
                    5,
                    Core.KMEANS_RANDOM_CENTERS,
                    centers
            );

            // --- 4. применяем к исходному изображению ---
            Mat result = new Mat(rows, cols, CvType.CV_8UC3);

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {

                    double[] pixel = lab.get(y, x);

                    // ищем ближайший центр
                    double minDist = Double.MAX_VALUE;
                    int best = 0;

                    for (int c = 0; c < centers.rows(); c++) {
                        double[] center = centers.get(c, 0);

                        double d =
                                Math.pow(pixel[0] - center[0], 2) +
                                        Math.pow(pixel[1] - center[1], 2) +
                                        Math.pow(pixel[2] - center[2], 2);

                        if (d < minDist) {
                            minDist = d;
                            best = c;
                        }
                    }

                    double[] bestCenter = centers.get(best, 0);
                    result.put(y, x, bestCenter);
                }
            }

            Imgproc.cvtColor(result, result, Imgproc.COLOR_Lab2BGR);
            return result;
        }

}
