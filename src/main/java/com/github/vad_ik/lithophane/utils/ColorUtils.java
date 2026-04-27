package com.github.vad_ik.lithophane.utils;

public class ColorUtils {
    private ColorUtils() {
    }

    public static boolean sameColor(double[] c1, double[] c2) {
        // после квантования можно строго сравнивать
        return c1[0] == c2[0] && c1[1] == c2[1] && c1[2] == c2[2];
    }

    public static String colorKey(double[] c) {
        return (int)c[0] + "_" + (int)c[1] + "_" + (int)c[2];
    }

    public static double[] parseColor(String key) {
        String[] parts = key.split("_");
        return new double[]{
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        };
    }
}
