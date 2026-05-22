package com.github.vad_ik.lithophane.service.methods.segmentations;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import com.github.vad_ik.lithophane.models.methods.ProcessingMethod;
import com.github.vad_ik.lithophane.models.methods.Type;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class Felzenszwalb implements ProcessingMethod {

    @Override
    public String getName() {
        return "Метод Felzenszwalb";
    }

    @Override
    public Type getType() {
        return Type.segmentations;
    }

    @Override
    public ArrayList<MethodsParam> getParams() {
        ArrayList<MethodsParam> params = new ArrayList<>(1);
        params.add(new MethodsParam(0, 100, 0.1, true, "параметр размытия Гаусса, уменьшает шум", 0.5));
        params.add(new MethodsParam(0, 1000, 0.1, true, "размер сегментов (чем больше k, тем крупнее сегменты)", 20));
        params.add(new MethodsParam(0, 1000, 1, false, "минимальный размер сегмента", 5));
        return params;
    }

    public Mat apply(Mat img, ArrayList<MethodsParam> params) {
        double sigma = params.get(0).getVal();
        double k = params.get(1).getVal();
        double minSize = params.get(2).getVal();
        return segment(img, sigma, k, (int) minSize);
    }

    // Метод для нахождения корня компонента (сжатие пути)
    private int find(List<Component> components, int x) {
        if (components.get(x).parent != x) {
            components.get(x).parent = find(components, components.get(x).parent);
        }
        return components.get(x).parent;
    }

    // Метод для объединения двух компонентов
    private boolean union(List<Component> components, int a, int b, double weight) {
        int rootA = find(components, a);
        int rootB = find(components, b);

        if (rootA == rootB) return false;

        Component compA = components.get(rootA);
        Component compB = components.get(rootB);

        // Проверяем условие объединения
        if (weight <= compA.threshold && weight <= compB.threshold) {
            // Объединяем меньший компонент с большим
            if (compA.size < compB.size) {
                compA.parent = rootB;
                components.get(rootB).size += compA.size;
                components.get(rootB).threshold = weight +
                        (compA.threshold - weight) * compB.size / (compA.size + compB.size);
            } else {
                compB.parent = rootA;
                components.get(rootA).size += compB.size;
                components.get(rootA).threshold = weight +
                        (compB.threshold - weight) * compA.size / (compA.size + compB.size);
            }
            return true;
        }
        return false;
    }

    /**
     * Алгоритм сегментации Felzenszwalb
     *
     * @param image   входное изображение (Mat)
     * @param sigma   параметр размытия (0.5-1.0)
     * @param k       параметр размера сегментов (100-500)
     * @param minSize минимальный размер сегмента
     * @return Mat с сегментированным изображением (каждый сегмент залит средним цветом)
     */
    public Mat segment(Mat image, double sigma, double k, int minSize) {
        // Применяем размытие по Гауссу
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(image, blurred, new Size(0, 0), sigma);

        int height = image.rows();
        int width = image.cols();
        int pixels = height * width;

        // Создаем граф (список ребер между соседними пикселями)
        List<Edge> edges = new ArrayList<>();

        // Добавляем ребра между горизонтальными соседями
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                int idx1 = y * width + x;
                int idx2 = y * width + (x + 1);
                double weight = calculateWeight(blurred, x, y, x + 1, y);
                edges.add(new Edge(idx1, idx2, weight));
            }
        }

        // Добавляем ребра между вертикальными соседями
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                int idx1 = y * width + x;
                int idx2 = (y + 1) * width + x;
                double weight = calculateWeight(blurred, x, y, x, y + 1);
                edges.add(new Edge(idx1, idx2, weight));
            }
        }

        // Сортируем ребра по весу
        Collections.sort(edges);

        // Инициализируем компоненты
        List<Component> components = new ArrayList<>(pixels);
        for (int i = 0; i < pixels; i++) {
            components.add(new Component(i, 1, k));
        }

        // Основной цикл алгоритма
        for (Edge edge : edges) {
            union(components, edge.u, edge.v, edge.weight);
        }

        // Объединяем маленькие компоненты
        List<Integer> sizes = new ArrayList<>(pixels);
        for (int i = 0; i < pixels; i++) {
            sizes.add(components.get(find(components, i)).size);
        }

        // Второй проход для объединения маленьких сегментов
        for (Edge edge : edges) {
            int rootU = find(components, edge.u);
            int rootV = find(components, edge.v);

            if (rootU != rootV &&
                    (components.get(rootU).size < minSize || components.get(rootV).size < minSize)) {
                union(components, rootU, rootV, Double.MAX_VALUE);
            }
        }

        // Создаем результат - заливаем каждый сегмент его средним цветом
        Mat result = new Mat();
        image.convertTo(result, CvType.CV_32FC3);

        // Вычисляем средний цвет для каждого компонента
        List<Mat> componentColors = new ArrayList<>();
        for (int i = 0; i < pixels; i++) {
            componentColors.add(null);
        }

        // Собираем суммы цветов для каждого компонента
        double[][] colorSums = new double[pixels][3];
        int[] pixelCounts = new int[pixels];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                int root = find(components, idx);
                double[] pixel = result.get(y, x);
                colorSums[root][0] += pixel[0];
                colorSums[root][1] += pixel[1];
                colorSums[root][2] += pixel[2];
                pixelCounts[root]++;
            }
        }

        // Вычисляем средние цвета
        double[][] avgColors = new double[pixels][3];
        for (int i = 0; i < pixels; i++) {
            if (pixelCounts[i] > 0) {
                avgColors[i][0] = colorSums[i][0] / pixelCounts[i];
                avgColors[i][1] = colorSums[i][1] / pixelCounts[i];
                avgColors[i][2] = colorSums[i][2] / pixelCounts[i];
            }
        }

        // Заполняем результат средними цветами
        Mat output = new Mat(image.size(), image.type());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                int root = find(components, idx);
                double[] color = avgColors[root];
                output.put(y, x, color);
            }
        }

        // Конвертируем обратно в исходный тип
        output.convertTo(output, image.type());

        return output;
    }

    /**
     * Вычисление веса ребра между двумя пикселями (евклидово расстояние в цветовом пространстве)
     */
    private double calculateWeight(Mat image, int x1, int y1, int x2, int y2) {
        double[] pixel1 = image.get(y1, x1);
        double[] pixel2 = image.get(y2, x2);

        double diff = 0;
        for (int i = 0; i < pixel1.length; i++) {
            diff += Math.pow(pixel1[i] - pixel2[i], 2);
        }
        return Math.sqrt(diff);
    }

    // Структура для хранения ребра графа
    private class Edge implements Comparable<Edge> {
        int u, v;
        double weight;

        Edge(int u, int v, double weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    // Структура для хранения информации о компоненте
    private class Component {
        int parent;
        int size;
        double threshold;

        Component(int parent, int size, double threshold) {
            this.parent = parent;
            this.size = size;
            this.threshold = threshold;
        }
    }
}
