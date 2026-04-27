package com.github.vad_ik.lithophane.service.methods.simplificationOfBoundaries;

import com.github.vad_ik.lithophane.models.methods.MethodsGen;
import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.github.vad_ik.lithophane.utils.ColorUtils.*;

@Slf4j
@Service
public class RemoveSmallRegions implements MethodsGen {

    @Override
    public String getName() {
        return "Удалить маленькие области";
    }
    @Override
    public Type getType() {
        return Type.simplificationOfBoundaries;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 5000, 1, false, "размер удаляемой области", 50));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {

        double size = params.get(0).getVal();

        return removeSmallRegions(img, (int) size);
    }
    public Mat removeSmallRegions(Mat input, int minSize) {
        Mat result = input.clone();

        int rows = result.rows();
        int cols = result.cols();

        boolean[][] visited = new boolean[rows][cols];

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                if (visited[y][x]) continue;

                double[] baseColor = result.get(y, x);

                List<Point> region = new ArrayList<>();
                Queue<Point> queue = new LinkedList<>();

                queue.add(new Point(x, y));
                visited[y][x] = true;

                // собираем компоненту
                while (!queue.isEmpty()) {
                    Point p = queue.poll();
                    region.add(p);

                    for (int i = 0; i < 4; i++) {
                        int nx = (int) p.x + dx[i];
                        int ny = (int) p.y + dy[i];

                        if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
                        if (visited[ny][nx]) continue;

                        double[] neighbor = result.get(ny, nx);

                        if (sameColor(baseColor, neighbor)) {
                            visited[ny][nx] = true;
                            queue.add(new Point(nx, ny));
                        }
                    }
                }

                // если маленькая область → перекрашиваем
                if (region.size() < minSize) {

                    Map<String, Integer> borderColorCount = new HashMap<>();

                    for (Point p : region) {
                        for (int i = 0; i < 4; i++) {
                            int nx = (int) p.x + dx[i];
                            int ny = (int) p.y + dy[i];

                            if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;

                            double[] neighbor = result.get(ny, nx);

                            if (!sameColor(baseColor, neighbor)) {
                                String key = colorKey(neighbor);
                                borderColorCount.put(key, borderColorCount.getOrDefault(key, 0) + 1);
                            }
                        }
                    }

                    // находим самый частый цвет вокруг
                    double[] bestColor = baseColor;
                    int maxCount = 0;

                    for (Map.Entry<String, Integer> entry : borderColorCount.entrySet()) {
                        if (entry.getValue() > maxCount) {
                            maxCount = entry.getValue();
                            bestColor = parseColor(entry.getKey());
                        }
                    }

                    // перекрашиваем
                    for (Point p : region) {
                        result.put((int) p.y, (int) p.x, bestColor);
                    }
                }
            }
        }

        return result;
    }
}
