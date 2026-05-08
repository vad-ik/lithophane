package com.github.vad_ik.lithophane.service.methods.simplificationOfBoundaries;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.vad_ik.lithophane.utils.ColorUtils.colorKey;
import static com.github.vad_ik.lithophane.utils.ColorUtils.parseColor;

@Slf4j
@Service
public class Majority implements MethodsGen {

    @Override
    public String getName() {
        return "Majority";
    }

    @Override
    public Type getType() {
        return Type.simplificationOfBoundaries;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 500, 1, false, "Радиус", 5));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {

        double size = params.get(0).getVal();

        return smoothByMajority(img, (int) size);
    }

    public Mat smoothByMajority(Mat input, int radius) {
        Mat result = input.clone();

        int rows = input.rows();
        int cols = input.cols();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                Map<String, Integer> freq = new HashMap<>();

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {

                        int ny = y + dy;
                        int nx = x + dx;

                        if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;

                        double[] c = input.get(ny, nx);
                        String key = colorKey(c);

                        freq.put(key, freq.getOrDefault(key, 0) + 1);
                    }
                }

                String bestKey = null;
                int max = 0;

                for (Map.Entry<String, Integer> e : freq.entrySet()) {
                    if (e.getValue() > max) {
                        max = e.getValue();
                        bestKey = e.getKey();
                    }
                }

                result.put(y, x, parseColor(bestKey));
            }
        }

        return result;
    }
}
