package com.github.vad_ik.lithophane.service.methods;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class VoidMethod implements MethodsGen {
    @Override
    public String getName() {
        return "Удалить блок";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        return null;
    }

    @Override
    public Mat apply(Mat mat, ArrayList<MethodsParam> params) {
        return null;
    }
}
