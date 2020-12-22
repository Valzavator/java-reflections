package com.maxsvynarchuk.metric;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Result {
    private List<ClassMetrics> classMetrics;

    private double sumOfInheritedAndNonOverrideMethods;
    private double sumOfAccessibleMethods;
    private double methodInheritanceFactor;

    private double sumOfPrivateMethods;
    private double sumOfOpenMethods;
    private double methodHidingFactor;

    private double sumOfPrivateFields;
    private double sumOfFields;
    private double attributeHidingFactor;

    private double sumOfInheritedAndNonOverrideFields;
    private double sumOfAccessibleFields;
    private double attributeInheritanceFactor;

    private double sumOfInheritedAndOverrideMethods;
    private double newMethodsCoefficient;
    private double polymorphismObjectFactor;

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
        System.out.print(">>> Method Inheritance Factor: ");
        if (Double.isNaN(methodInheritanceFactor)) {
            System.out.println("No accessible methods");
        } else {
            System.out.printf("%f (%.0f / %.0f)\n",
                    methodInheritanceFactor,
                    sumOfInheritedAndNonOverrideMethods,
                    sumOfAccessibleMethods);
        }
    }

    public void printMethodHidingFactor() {
        System.out.print(">>> Method Hiding Factor: ");
        if (Double.isNaN(methodHidingFactor)) {
            System.out.println("No accessible methods");
        } else {
            System.out.printf("%f (%.0f / [%.0f + %.0f])\n",
                    methodHidingFactor,
                    sumOfPrivateMethods,
                    sumOfPrivateMethods,
                    sumOfOpenMethods);
        }
    }

    public void printAttributeHidingFactor() {
        System.out.print(">>> Attribute Hiding Factor: ");
        if (Double.isNaN(attributeHidingFactor)) {
            System.out.println("No accessible fields");
        } else {
            System.out.printf("%f (%.0f / %.0f)\n",
                    attributeHidingFactor,
                    sumOfPrivateFields,
                    sumOfFields);
        }
    }

    public void printAttributeInheritanceFactor() {
        System.out.print(">>> Attribute Inheritance Factor: ");
        if (Double.isNaN(attributeInheritanceFactor)) {
            System.out.println("No accessible fields");
        } else {
            System.out.printf("%f (%.0f / %.0f)\n",
                    attributeInheritanceFactor,
                    sumOfInheritedAndNonOverrideFields,
                    sumOfAccessibleFields);
        }
    }

    public void printPolymorphismObjectFactor() {
        System.out.print(">>> Polymorphism Object Factor: ");
        if (Double.isNaN(polymorphismObjectFactor)) {
            System.out.println("No accessible methods");
        } else {
            System.out.printf("%f (%.0f / %.0f)\n",
                    polymorphismObjectFactor,
                    sumOfInheritedAndOverrideMethods,
                    newMethodsCoefficient);
        }
    }
}
