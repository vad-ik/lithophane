package com.github.vad_ik.lithophane.service.methods.Filter;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.github.vad_ik.lithophane.utils.ExceptionUtils.throwIfNumberOfParametersIsNotEqual;

@Service
@Slf4j
public class NormalizedBlockFilter implements MethodsGen {


    @Override
    public String getName() {
        return "Нормализованный блочный фильтр";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params=new ArrayList<>(2);
        params.add(new MethodsParam(0,1000,1,false,"Высота окна",5));
        params.add(new MethodsParam(0,1000,1,false,"Ширина окна",5));
        return params;
    }

    /**
     * @param original обрабатываемое изображение
     * @param w
     * @param h        Размер используемого ядра (количество рассматриваемых соседей).
     **/
    public Mat apply(Mat original, ArrayList<MethodsParam> params) {//w,h - ширина и высота окна для фильтрации

        throwIfNumberOfParametersIsNotEqual(params,2, getName());
        int w = (int) params.get(0).getVal();
        int h = (int) params.get(1).getVal();
        Mat out = new Mat();
        Imgproc.blur(original, out, new Size(w, h), new Point(-1, -1));
        return out;
    }
}
