//package com.github.vad_ik.lithophane.service.methods;
//
//import com.github.vad_ik.lithophane.models.methods.MethodsGen;
//import com.github.vad_ik.lithophane.models.methods.MethodsParam;
//import lombok.extern.slf4j.Slf4j;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.TermCriteria;
//import org.opencv.imgproc.Imgproc;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.ximgproc.SuperpixelSLIC;
//import org.opencv.ximgproc.Ximgproc;
//
//
//@Slf4j
//@Service
//public class SLICClustering  implements MethodsGen {
//
//    @Override
//    public String getName() {
//        return "Метод k-средних (с нормализацией)(k-means clustering)";
//    }
//
//    @Override
//    public ArrayList<MethodsParam> getParams() {
//        ArrayList<MethodsParam> params = new ArrayList<>(1);
//        params.add(new MethodsParam(0, 1000, 1, false, "Количество кластеров", 5));
//        params.add(new MethodsParam(0, 1000, 1, false, "нормализация (меньше - сильнее)", 5));
//        return params;
//    }
//
//    public Mat apply(Mat img, ArrayList<MethodsParam> params) {
//        int k = (int) params.get(0).getVal();
//        int maxPerColor = (int) params.get(1).getVal();
//
//        return slic (img,k,maxPerColor);
//
//    }
//
//
//
//    public static Mat slic(Mat img, int regionSize, float ruler) {
//
//        Mat lab = new Mat();
//        Imgproc.cvtColor(img, lab, Imgproc.COLOR_BGR2Lab);
//
//        // --- 1. создаем SLIC ---
//        SuperpixelSLIC slic = Ximgproc.createSuperpixelSLIC(
//                lab,
//                Ximgproc.SLICO,   // лучший вариант
//                regionSize,       // размер сегмента (10-40)
//                ruler             // баланс цвет/пространство
//        );
//
//        slic.iterate(10);
//
//        // --- 2. получаем метки ---
//        Mat labels = new Mat();
//        slic.getLabels(labels);
//
//        int numLabels = slic.getNumberOfSuperpixels();
//
//        // --- 3. считаем средний цвет сегмента ---
//        double[][] sum = new double[numLabels][3];
//        int[] count = new int[numLabels];
//
//        for (int y = 0; y < img.rows(); y++) {
//            for (int x = 0; x < img.cols(); x++) {
//                int label = (int) labels.get(y, x)[0];
//                double[] pixel = lab.get(y, x);
//
//                sum[label][0] += pixel[0];
//                sum[label][1] += pixel[1];
//                sum[label][2] += pixel[2];
//                count[label]++;
//            }
//        }
//
//        double[][] centers = new double[numLabels][3];
//        for (int i = 0; i < numLabels; i++) {
//            centers[i][0] = sum[i][0] / count[i];
//            centers[i][1] = sum[i][1] / count[i];
//            centers[i][2] = sum[i][2] / count[i];
//        }
//
//        // --- 4. собираем результат ---
//        Mat result = new Mat(img.size(), CvType.CV_8UC3);
//
//        for (int y = 0; y < img.rows(); y++) {
//            for (int x = 0; x < img.cols(); x++) {
//                int label = (int) labels.get(y, x)[0];
//                result.put(y, x, centers[label]);
//            }
//        }
//
//        Imgproc.cvtColor(result, result, Imgproc.COLOR_Lab2BGR);
//        return result;
//    }
//
//}
