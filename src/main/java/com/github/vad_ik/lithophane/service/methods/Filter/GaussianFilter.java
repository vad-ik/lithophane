package com.github.vad_ik.lithophane.service.methods.Filter;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.github.vad_ik.lithophane.utils.ExceptionUtils.throwIfNumberOfParametersIsNotEqual;

@Service
@Slf4j
public class GaussianFilter implements MethodsGen {

    @Override
    public String getName() {
        return "Гаусовский фильтр";
    }

    @Override
    public Type getType() {
        return Type.smoothing;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(4);
        params.add(new MethodsParam(1, 1001, 2, false, "Высота окна", 5));
        params.add(new MethodsParam(1, 1001, 2, false, "Ширина окна", 5));

        params.add(new MethodsParam(0, 1000, 0.1, true, "Стандартное отклонение по x", 0));
        params.add(new MethodsParam(0, 1000, 0.1, true, "Стандартное отклонение по y", 0));
        return params;
    }

    /**
     * @param original обрабатываемое изображение
     * @param w
     * @param h        Размер используемого ядра (количество рассматриваемых соседей).
     *                 должны   быть нечетными и положительными числами, иначе размер будет рассчитан с использованием sigmaX и sigmaY
     * @param sigmaX   Стандартное отклонение x. подразумевает, что рассчитывается с использованием размера ядра.
     * @param sigmaY   Стандартное отклонение по y. подразумевает, что рассчитывается с использованием размера ядра.
     **/
    public Mat apply(Mat original, ArrayList<MethodsParam> params) {

        throwIfNumberOfParametersIsNotEqual(params, 4, getName());
        int w = (int) params.get(0).getVal();
        int h = (int) params.get(1).getVal();
        double sigmaX = params.get(2).getVal();
        double sigmaY = params.get(3).getVal();

        if (w <= 0 || w % 2 != 1 || h <= 0 || h % 2 != 1) {
            log.error("получены некоректные данные для Гауссовского фильтра {},{},{},{}", w, h, sigmaX, sigmaY);
        }
        Imgproc.GaussianBlur(original, original, new Size(w, h), sigmaX, sigmaY);
        return original;
    }
}
