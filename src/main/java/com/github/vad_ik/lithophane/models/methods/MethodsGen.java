package com.github.vad_ik.lithophane.models.methods;

import org.opencv.core.Mat;

import java.util.ArrayList;

public interface MethodsGen {
    public String getName();
    public Type getType();
    public ArrayList<MethodsParam> getParams();
    public Mat apply(Mat mat, ArrayList<MethodsParam> params);
}
