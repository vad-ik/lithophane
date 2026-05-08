package com.github.vad_ik.lithophane.service.methods.segmentations;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class MedianCutQuantization implements MethodsGen {

    @Override
    public String getName() {
        return "Метод Median Cut";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 1000, 1, false, "Количество кластеров", 5));
        return params;
    }

    @Override
    public Type getType() {
        return Type.segmentations;
    }


    public Mat apply(Mat img, ArrayList<MethodsParam> params) {
        int k = (int) params.get(0).getVal();

        return applyMedianCut(img, k);
    }

    public Mat applyMedianCut(Mat input, int k) {
        Mat img = new Mat();
        input.convertTo(img, CvType.CV_32F, 1.0 / 255.0);

        // можно включить LAB для лучшего результата
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2Lab);

        List<double[]> allPixels = new ArrayList<>();

        for (int y = 0; y < img.rows(); y++) {
            for (int x = 0; x < img.cols(); x++) {
                allPixels.add(img.get(y, x));
            }
        }

        List<ColorBox> boxes = new ArrayList<>();
        boxes.add(new ColorBox(allPixels));

        while (boxes.size() < k) {
            // выбираем самый "растянутый" бокс
            ColorBox boxToSplit = Collections.max(boxes, Comparator.comparingDouble(
                    b -> Math.max(Math.max(b.rMax - b.rMin, b.gMax - b.gMin), b.bMax - b.bMin)
            ));

            boxes.remove(boxToSplit);

            ColorBox[] split = boxToSplit.splitBox();
            boxes.add(split[0]);
            boxes.add(split[1]);
        }

        // получаем палитру
        List<double[]> palette = new ArrayList<>();
        for (ColorBox box : boxes) {
            palette.add(box.getAverageColor());
        }

        // перекрашивание
        Mat result = new Mat(img.size(), img.type());

        for (int y = 0; y < img.rows(); y++) {
            for (int x = 0; x < img.cols(); x++) {
                double[] pixel = img.get(y, x);

                double minDist = Double.MAX_VALUE;
                double[] bestColor = null;

                for (double[] color : palette) {
                    double d0 = pixel[0] - color[0];
                    double d1 = pixel[1] - color[1];
                    double d2 = pixel[2] - color[2];
                    double dist = d0 * d0 + d1 * d1 + d2 * d2;

                    if (dist < minDist) {
                        minDist = dist;
                        bestColor = color;
                    }
                }

                result.put(y, x, bestColor);
            }
        }

        // обратно в BGR
        Imgproc.cvtColor(result, result, Imgproc.COLOR_Lab2BGR);
        result.convertTo(result, CvType.CV_8U, 255.0);


        return result;
    }

    static class ColorBox {
        List<double[]> pixels;

        double rMin, rMax, gMin, gMax, bMin, bMax;

        ColorBox(List<double[]> pixels) {
            this.pixels = pixels;
            computeBounds();
        }

        void computeBounds() {
            rMin = gMin = bMin = Double.MAX_VALUE;
            rMax = gMax = bMax = Double.MIN_VALUE;

            for (double[] p : pixels) {
                rMin = Math.min(rMin, p[0]);
                rMax = Math.max(rMax, p[0]);
                gMin = Math.min(gMin, p[1]);
                gMax = Math.max(gMax, p[1]);
                bMin = Math.min(bMin, p[2]);
                bMax = Math.max(bMax, p[2]);
            }
        }

        int longestChannel() {
            double rRange = rMax - rMin;
            double gRange = gMax - gMin;
            double bRange = bMax - bMin;

            if (rRange >= gRange && rRange >= bRange) return 0;
            if (gRange >= rRange && gRange >= bRange) return 1;
            return 2;
        }

        ColorBox[] splitBox() {
            int channel = longestChannel();

            pixels.sort(Comparator.comparingDouble(p -> p[channel]));

            int median = pixels.size() / 2;

            List<double[]> first = pixels.subList(0, median);
            List<double[]> second = pixels.subList(median, pixels.size());

            return new ColorBox[]{
                    new ColorBox(new ArrayList<>(first)),
                    new ColorBox(new ArrayList<>(second))
            };
        }

        double[] getAverageColor() {
            double r = 0, g = 0, b = 0;

            for (double[] p : pixels) {
                r += p[0];
                g += p[1];
                b += p[2];
            }

            int size = pixels.size();
            return new double[]{r / size, g / size, b / size};
        }
    }
}