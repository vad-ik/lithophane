package com.github.vad_ik.lithophane.models.methods;

import org.opencv.core.Mat;

import java.util.ArrayList;

public interface MethodsGen {
    String getName();

    Type getType();

    ArrayList<MethodsParam> getParams();

    Mat apply(Mat mat, ArrayList<MethodsParam> params);
}
