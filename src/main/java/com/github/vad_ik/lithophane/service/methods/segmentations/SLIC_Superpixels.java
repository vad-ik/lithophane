package com.github.vad_ik.lithophane.service.methods.segmentations;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SLIC_Superpixels implements MethodsGen {

    public static Mat slic(Mat input, int regionSize, float m, int iterations) {
        Mat lab = new Mat();
        Imgproc.cvtColor(input, lab, Imgproc.COLOR_BGR2Lab);

        int width = lab.cols();
        int height = lab.rows();

        int S = regionSize;

        int nx = width / S;
        int ny = height / S;

        List<double[]> centers = new ArrayList<>();
        List<int[]> centerXY = new ArrayList<>();

        // 1. инициализация центров по сетке
        for (int y = S / 2; y < height; y += S) {
            for (int x = S / 2; x < width; x += S) {
                double[] color = lab.get(y, x);
                centers.add(new double[]{color[0], color[1], color[2]});
                centerXY.add(new int[]{x, y});
            }
        }

        int K = centers.size();

        Mat labels = new Mat(height, width, CvType.CV_32S);
        Mat distances = new Mat(height, width, CvType.CV_64F);

        distances.setTo(new Scalar(Double.MAX_VALUE));

        double invWT = (m / S) * (m / S);

        for (int iter = 0; iter < iterations; iter++) {

            // 2. assignment
            for (int k = 0; k < K; k++) {
                double[] c = centers.get(k);
                int cx = centerXY.get(k)[0];
                int cy = centerXY.get(k)[1];

                int xStart = Math.max(cx - S, 0);
                int xEnd = Math.min(cx + S, width);
                int yStart = Math.max(cy - S, 0);
                int yEnd = Math.min(cy + S, height);

                for (int y = yStart; y < yEnd; y++) {
                    for (int x = xStart; x < xEnd; x++) {

                        double[] color = lab.get(y, x);

                        double dc0 = color[0] - c[0];
                        double dc1 = color[1] - c[1];
                        double dc2 = color[2] - c[2];

                        double dColor = dc0 * dc0 + dc1 * dc1 + dc2 * dc2;

                        double dx = x - cx;
                        double dy = y - cy;
                        double dSpace = dx * dx + dy * dy;

                        double D = dColor + invWT * dSpace;

                        double prev = distances.get(y, x)[0];
                        if (D < prev) {
                            distances.put(y, x, D);
                            labels.put(y, x, k);
                        }
                    }
                }
            }

            // 3. обновление центров
            double[][] sumColor = new double[K][3];
            double[][] sumXY = new double[K][2];
            int[] count = new int[K];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int label = (int) labels.get(y, x)[0];
                    double[] color = lab.get(y, x);

                    sumColor[label][0] += color[0];
                    sumColor[label][1] += color[1];
                    sumColor[label][2] += color[2];

                    sumXY[label][0] += x;
                    sumXY[label][1] += y;

                    count[label]++;
                }
            }

            for (int k = 0; k < K; k++) {
                if (count[k] == 0) continue;

                centers.get(k)[0] = sumColor[k][0] / count[k];
                centers.get(k)[1] = sumColor[k][1] / count[k];
                centers.get(k)[2] = sumColor[k][2] / count[k];

                centerXY.get(k)[0] = (int) (sumXY[k][0] / count[k]);
                centerXY.get(k)[1] = (int) (sumXY[k][1] / count[k]);
            }

            distances.setTo(new Scalar(Double.MAX_VALUE));
        }

        // 4. визуализация (средний цвет)
        Mat resultLab = new Mat(height, width, lab.type());

        double[][] avg = new double[K][3];
        int[] count = new int[K];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = (int) labels.get(y, x)[0];
                double[] color = lab.get(y, x);

                avg[label][0] += color[0];
                avg[label][1] += color[1];
                avg[label][2] += color[2];
                count[label]++;
            }
        }

        for (int k = 0; k < K; k++) {
            if (count[k] == 0) continue;
            avg[k][0] /= count[k];
            avg[k][1] /= count[k];
            avg[k][2] /= count[k];
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = (int) labels.get(y, x)[0];
                resultLab.put(y, x, avg[label]);
            }
        }

        Mat result = new Mat();
        Imgproc.cvtColor(resultLab, result, Imgproc.COLOR_Lab2BGR);

        return result;
    }

    @Override
    public String getName() {
        return "Метод SLIC_Superpixels";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 1000, 1, false, "regionSize", 25));
        params.add(new MethodsParam(0, 1000, 0.1, true, "ruler", 5));
        params.add(new MethodsParam(0, 1000, 1, false, "mergeThreshold", 15));
        return params;
    }

    @Override
    public Type getType() {
        return Type.segmentations;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {
        int regionSize = (int) params.get(0).getVal(); // spatial radius
        float m = (float) params.get(1).getVal(); // color radius
        int iterations = (int) params.get(2).getVal();

        return slic(img, regionSize, m, iterations);
    }
}
