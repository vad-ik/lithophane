package com.github.vad_ik.lithophane.models.methods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {
    segmentations("Кластеризация"),
    smoothing("Сглаживание"),
    simplificationOfBoundaries("Упрощение границ"),
    comand("");

 private final    String name;
}
