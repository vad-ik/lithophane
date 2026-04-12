package com.github.vad_ik.lithophane.service.methods;
import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StainedGlassEffect  implements MethodsGen {

    public static Mat applyStainedGlass(Mat img, int k) {

        // 1. Сглаживание (bilateral filter)
        Mat smooth = new Mat();
        Imgproc.bilateralFilter(img, smooth, 9, 75, 75);

        // 2. Перевод в LAB
        Mat lab = new Mat();
        Imgproc.cvtColor(smooth, lab, Imgproc.COLOR_BGR2Lab);

        // 3. Подготовка данных для KMeans
        Mat data = lab.reshape(1, lab.rows() * lab.cols());
        data.convertTo(data, CvType.CV_32F);

        // 4. KMeans
        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(
                TermCriteria.EPS + TermCriteria.MAX_ITER,
                20,
                1.0
        );

        Mat centers = new Mat();
        Core.kmeans(data, k, labels, criteria, 10, Core.KMEANS_PP_CENTERS, centers);

        // 5. Восстановление изображения
        centers.convertTo(centers, CvType.CV_8U);

        byte[] centersData = new byte[(int) (centers.total() * centers.channels())];
        centers.get(0, 0, centersData);

        int rows = img.rows();
        int cols = img.cols();

        Mat segmented = new Mat(rows, cols, CvType.CV_8UC3);

        int[] labelsData = new int[(int) labels.total()];
        labels.get(0, 0, labelsData);

        for (int i = 0; i < labelsData.length; i++) {
            int clusterIdx = labelsData[i];
            byte b = centersData[clusterIdx * 3];
            byte g = centersData[clusterIdx * 3 + 1];
            byte r = centersData[clusterIdx * 3 + 2];

            int row = i / cols;
            int col = i % cols;

            segmented.put(row, col, new byte[]{b, g, r});
        }

        // 6. Границы
        Mat gray = new Mat();
        Imgproc.cvtColor(segmented, gray, Imgproc.COLOR_BGR2GRAY);

        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 100, 200);

        // утолщение линий
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(edges, edges, kernel);

        // 7. Наложение черных линий
        Mat result = segmented.clone();

        for (int y = 0; y < edges.rows(); y++) {
            for (int x = 0; x < edges.cols(); x++) {
                double[] edgePixel = edges.get(y, x);
                if (edgePixel[0] > 0) {
                    result.put(y, x, new byte[]{0, 0, 0});
                }
            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "методы витража";
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 1000, 1, false, "Количество сегментов", 5));
        return params;
    }

    @Override
    public Mat apply(Mat mat, ArrayList<MethodsParam> params) {
        int k = (int) params.getFirst().getVal();
        Mat result =  applyStainedGlass(mat,  k);
        return result;
    }
}