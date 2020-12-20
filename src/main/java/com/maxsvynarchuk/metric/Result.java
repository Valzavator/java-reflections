package com.maxsvynarchuk.metric;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Result {
    private final List<ClassMetrics> classMetrics;
    private final double methodInheritanceFactor;
    private final double methodHidingFactor;
    private final double attributeHidingFactor;
    private final double attributeInheritanceFactor;
    private final double polymorphismObjectFactor;

    public void printAll() {
        printDepthOfInheritanceTree();
        System.out.println();
        printNumberOfChildren();
        System.out.println();
        printMethodInheritanceFactor();
        System.out.println();
        printMethodHidingFactor();
        System.out.println();
        printAttributeHidingFactor();
        System.out.println();
        printAttributeInheritanceFactor();
        System.out.println();
        printPolymorphismObjectFactor();
    }

    public void printDepthOfInheritanceTree() {
        System.out.println(">>> Depth of Inheritance Tree:");
        classMetrics.stream().filter(cm -> cm.getDepthOfInheritanceTree() >= 0)
                .forEach(cm -> System.out.println(cm.getDepthOfInheritanceTree() + " > " + cm.getClassName()));
    }

    public void printNumberOfChildren() {
        System.out.println(">>> Number of children:");
        classMetrics.forEach(cm -> System.out.println(cm.getNumOfChildren() + " > " + cm.getClassName()));
    }

    public void printMethodInheritanceFactor() {
        String res = Double.isNaN(methodInheritanceFactor)
                ? "No accessible methods"
                : String.valueOf(methodInheritanceFactor);
        System.out.println(">>> Method Inheritance Factor: " + res);
    }

    public void printMethodHidingFactor() {
        System.out.println(">>> Method Hiding Factor: " + methodHidingFactor);
    }

    public void printAttributeHidingFactor() {
        System.out.println(">>> Attribute Hiding Factor: " + attributeHidingFactor);
    }

    public void printAttributeInheritanceFactor() {
        String res = Double.isNaN(attributeInheritanceFactor)
                ? "No accessible fields"
                : String.valueOf(attributeInheritanceFactor);
        System.out.println(">>> Attribute Inheritance Factor: " + res);
    }

    public void printPolymorphismObjectFactor() {
        System.out.println(">>> Polymorphism Object Factor: " + polymorphismObjectFactor);
    }
}
