package com.github.vad_ik.lithophane.utils;

import com.github.vad_ik.lithophane.models.methods.MethodsParam;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class ExceptionUtils {

    public static void throwIfNumberOfParametersIsNotEqual(ArrayList<MethodsParam> params, int required, String nameMethod) {
        if (required != params.size()) {
            String err = "Для метода " + nameMethod + " ожидалось " + required + " параметров, а пришло " + params.size();
            log.error(err);
            throw new IllegalArgumentException(err);
        }
    }
}
