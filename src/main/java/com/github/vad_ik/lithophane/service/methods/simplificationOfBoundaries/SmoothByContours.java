package com.github.vad_ik.lithophane.service.methods.simplificationOfBoundaries;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.github.vad_ik.lithophane.utils.ColorUtils.*;

@Slf4j
@Service
public class SmoothByContours implements MethodsGen {

    @Override
    public String getName() {
        return "SmoothByContours";
    }

    @Override
    public Type getType() {
        return Type.simplificationOfBoundaries;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 5, 0.0001, true, "epsilonFactor", 0.001));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {

        double size = params.get(0).getVal();

        return smoothByContours(img, size);
    }

    public Mat smoothByContours(Mat input, double epsilonFactor) {
        Mat result = Mat.zeros(input.size(), input.type());

        int rows = input.rows();
        int cols = input.cols();

        // собрать уникальные цвета
        Set<String> uniqueColors = new HashSet<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                uniqueColors.add(colorKey(input.get(y, x)));
            }
        }

        for (String key : uniqueColors) {
            double[] color = parseColor(key);

            // создаём маску для текущего цвета
            Mat mask = new Mat(rows, cols, CvType.CV_8UC1);
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    if (sameColor(input.get(y, x), color)) {
                        mask.put(y, x, 255);
                    } else {
                        mask.put(y, x, 0);
                    }
                }
            }

            // ищем контуры
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(
                    mask,
                    contours,
                    hierarchy,
                    Imgproc.RETR_CCOMP,
                    Imgproc.CHAIN_APPROX_NONE
            );

            for (int i = 0; i < contours.size(); i++) {

                MatOfPoint contour = contours.get(i);
                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

                double perimeter = Imgproc.arcLength(contour2f, true);
                double epsilon = epsilonFactor * perimeter;

                MatOfPoint2f approx2f = new MatOfPoint2f();
                Imgproc.approxPolyDP(contour2f, approx2f, epsilon, true);

                MatOfPoint approx = new MatOfPoint(approx2f.toArray());

                // если внешний контур
                double[] h = hierarchy.get(0, i);
                int parent = (int) h[3];

                if (parent < 0) {
                    // заливаем цветом
                    Imgproc.drawContours(result, List.of(approx), -1, new Scalar(color), -1);
                } else {
                    // это "дырка" — заливаем чёрным (или фоном)
                    Imgproc.drawContours(result, List.of(approx), -1, new Scalar(0, 0, 0), -1);
                }
            }
        }

        return result;
    }
}
