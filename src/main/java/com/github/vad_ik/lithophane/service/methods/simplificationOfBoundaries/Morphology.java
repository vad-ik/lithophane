package com.github.vad_ik.lithophane.service.methods.simplificationOfBoundaries;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class Morphology implements MethodsGen {

    @Override
    public String getName() {
        return "Morphology";
    }
    @Override
    public Type getType() {
        return Type.simplificationOfBoundaries;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 50, 1, false, "размер ядра", 5));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {

        double size = params.get(0).getVal();

        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_ELLIPSE,
                new Size(size, size)
        );

        Mat result = new Mat();
        Imgproc.morphologyEx(img, result, Imgproc.MORPH_CLOSE, kernel);

        return result;
    }
}
