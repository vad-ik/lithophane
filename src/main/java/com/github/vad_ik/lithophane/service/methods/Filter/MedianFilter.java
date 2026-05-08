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
public class MedianFilter implements MethodsGen {


    @Override
    public String getName() {
        return "Медианный фильтр";
    }

    @Override
    public Type getType() {
        return Type.smoothing;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(1, 1001, 2, false, "Ширина окна", 5));
        return params;
    }

    /**
     * @param original обрабатываемое изображение
     * @param h        ширина и высота окна для фильтрации//должен быть нечетным
     **/
    public Mat apply(Mat original, ArrayList<MethodsParam> params) {
        throwIfNumberOfParametersIsNotEqual(params, 1, getName());
        int h = (int) params.get(0).getVal();
        if (h <= 0 || h % 2 != 1) {
            log.error("получены некоректные данные для медианного фильтра {}", h);
        }
        Imgproc.medianBlur(original, original, h);
        return original;
    }
}
