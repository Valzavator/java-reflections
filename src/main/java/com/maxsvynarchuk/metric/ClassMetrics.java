package com.maxsvynarchuk.metric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class ClassMetrics {
    @Setter(AccessLevel.NONE)
    private final Class<?> clazz;
    @Setter(AccessLevel.NONE)
    private final String className;

    private int depthOfInheritanceTree;
    private int numOfChildren;

    // MIF
    private int numOfInheritedAndNonOverrideMethods;
    private int numOfAccessibleMethods;

    // MHF
    private int numOfPrivateMethods;
    private int numOfOpenMethods;

    // AIF
    private int numOfInheritedAndNonOverrideFields;
    private int numOfAccessibleFields;

    // AHF
    private int numOfPrivateFields;
    private int numOfFields;

    // POF
    private int numOfInheritedAndOverrideMethods;
    private int numOfNewMethods;

    public ClassMetrics(Class<?> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
        this.className = clazz.getName();
    }
}
