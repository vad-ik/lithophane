package com.github.vad_ik.lithophane.service.methods.Filter;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.github.vad_ik.lithophane.utils.ExceptionUtils.throwIfNumberOfParametersIsNotEqual;

@Service
@Slf4j
public class BilateralFilter implements MethodsGen {


    @Override
    public String getName() {
        return "Двусторонний фильтр";
    }

    @Override
    public Type getType() {
        return Type.smoothing;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(4);
        params.add(new MethodsParam(1, 1001, 2, false, "Диаметр  окна (нечетное)", 5));

        params.add(new MethodsParam(0, 1000, 0.1, true, "Отклонение в цветовом пространстве (небольшие значения ≈ 10 сохраняют границы, большие значения ≈ 100 размывают объекты)", 0));
        params.add(new MethodsParam(0, 1000, 0.1, true, "Отклонение в координатном пространстве (больше значения имеет ближайшие пиксели)", 0));
        return params;
    }

    /**
     * @param original    обрабатываемое изображение
     * @param d           : Диаметр окрестности каждого пикселя.
     * @param sigmaColor: Стандартное отклонение в цветовом пространстве.
     * @param sigmaSpace: Стандартное отклонение в координатном пространстве (в пикселях)
     **/
    public Mat apply(Mat original, ArrayList<MethodsParam> params) {

        throwIfNumberOfParametersIsNotEqual(params, 3, getName());
        int d = (int) params.get(0).getVal();
        double sigmaColor = params.get(1).getVal();
        double sigmaSpace = params.get(2).getVal();

        Mat result = new Mat();
        Imgproc.bilateralFilter(original, result, d, sigmaColor, sigmaSpace);
        return result;
    }
}
