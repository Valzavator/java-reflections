package com.maxsvynarchuk.metric;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maxsvynarchuk.metric.ReflectionHelper.*;

public class Analyzer {
    private final String packagePath;
    private Reflections reflections;

    public Analyzer(String packagePath) {
        this.packagePath = Objects.requireNonNull(packagePath);
        initReflections();
    }

    private void initReflections() {
        reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packagePath))
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packagePath))));
    }

    public Result calculateMetrics() {
        Set<Class<?>> classes = getAllClasses();
        List<ClassMetrics> classMetrics = getAllMetricsForClasses(classes);

        Result result = Result.builder()
                .classMetrics(classMetrics)
                .build();

        calculateMethodInheritanceFactor(classMetrics, result);
        calculateMethodHidingFactor(classMetrics, result);
        calculateAttributeHidingFactor(classMetrics, result);
        calculateAttributeInheritanceFactor(classMetrics, result);
        calculatePolymorphismObjectFactor(classMetrics, result);

        return result;
    }

    private void calculateMethodInheritanceFactor(List<ClassMetrics> classMetrics, Result result) {
        double sumOfInheritedAndNonOverrideMethods = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfInheritedAndNonOverrideMethods)
                .sum();
        double sumOfAccessibleMethods = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfAccessibleMethods)
                .sum();
        result.setSumOfInheritedAndNonOverrideMethods(sumOfInheritedAndNonOverrideMethods);
        result.setSumOfAccessibleMethods(sumOfAccessibleMethods);
        result.setMethodInheritanceFactor(sumOfInheritedAndNonOverrideMethods / sumOfAccessibleMethods);
    }

    private void calculateMethodHidingFactor(List<ClassMetrics> classMetrics, Result result) {
        double sumOfPrivateMethods = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfPrivateMethods)
                .sum();
        double sumOfOpenMethods = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfOpenMethods)
                .sum();
        result.setSumOfPrivateMethods(sumOfPrivateMethods);
        result.setSumOfOpenMethods(sumOfOpenMethods);
        result.setMethodHidingFactor(sumOfPrivateMethods / (sumOfPrivateMethods + sumOfOpenMethods));
    }

    private void calculateAttributeHidingFactor(List<ClassMetrics> classMetrics, Result result) {
        double numOfPrivateFields = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfPrivateFields)
                .sum();
        double numOfFields = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfFields)
                .sum();
        result.setSumOfPrivateFields(numOfPrivateFields);
        result.setSumOfFields(numOfFields);
        result.setAttributeHidingFactor(numOfPrivateFields / numOfFields);
    }

    private void calculateAttributeInheritanceFactor(List<ClassMetrics> classMetrics, Result result) {
        double sumOfInheritedAndNonOverrideFields = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfInheritedAndNonOverrideFields)
                .sum();
        double sumOfAccessibleFields = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfAccessibleFields)
                .sum();
        result.setSumOfInheritedAndNonOverrideFields(sumOfInheritedAndNonOverrideFields);
        result.setSumOfAccessibleFields(sumOfAccessibleFields);
        result.setAttributeInheritanceFactor(sumOfInheritedAndNonOverrideFields / sumOfAccessibleFields);
    }

    private void calculatePolymorphismObjectFactor(List<ClassMetrics> classMetrics, Result result) {
        double sumOfInheritedAndOverrideMethods = classMetrics.stream()
                .mapToDouble(ClassMetrics::getNumOfInheritedAndOverrideMethods)
                .sum();
        double newMethodsCoefficient = classMetrics.stream()
                .mapToDouble(cm -> cm.getNumOfNewMethods() * cm.getNumOfChildren())
                .sum();
        result.setSumOfInheritedAndOverrideMethods(sumOfInheritedAndOverrideMethods);
        result.setNewMethodsCoefficient(newMethodsCoefficient);
        result.setPolymorphismObjectFactor(sumOfInheritedAndOverrideMethods / newMethodsCoefficient);
    }

    private List<ClassMetrics> getAllMetricsForClasses(Collection<Class<?>> classes) {
        return classes.stream()
                .map(clazz -> {
                    ClassMetrics cm = new ClassMetrics(clazz);
                    cm.setDepthOfInheritanceTree(calculateDepthOfInheritanceTree(clazz));
                    cm.setNumOfChildren(calculateNumberOfChildren(clazz));
                    calculateMethodInheritanceCoefficients(cm);
                    calculateMethodHidingCoefficients(cm);
                    calculateAttributeInheritanceCoefficients(cm);
                    calculateAttributeHidingCoefficients(cm);
                    calculatePolymorphismObjectCoefficients(cm);
                    return cm;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    private void calculateMethodInheritanceCoefficients(ClassMetrics classMetrics) {
        Class<?> clazz = classMetrics.getClazz();

        Set<Method> accessibleMethods = getAccessibleMethods(clazz);
        Set<Method> inheritedAndNonOverrideMethods = accessibleMethods.stream()
                .filter(accessibleMethod -> getDeclaredMethods(clazz).stream()
                        .noneMatch(declaredMethod -> equalMethods(accessibleMethod, declaredMethod)))
                .collect(Collectors.toUnmodifiableSet());

        classMetrics.setNumOfAccessibleMethods(accessibleMethods.size());
        classMetrics.setNumOfInheritedAndNonOverrideMethods(inheritedAndNonOverrideMethods.size());
    }

    private void calculateMethodHidingCoefficients(ClassMetrics classMetrics) {
        Class<?> clazz = classMetrics.getClazz();

        Set<Method> privateMethods = getPrivateMethods(clazz);
        Set<Method> openMethods = getOpenMethods(clazz);

        classMetrics.setNumOfPrivateMethods(privateMethods.size());
        classMetrics.setNumOfOpenMethods(openMethods.size());
    }

    private void calculateAttributeInheritanceCoefficients(ClassMetrics classMetrics) {
        Class<?> clazz = classMetrics.getClazz();

        Set<Field> accessibleFields = getAccessibleFields(clazz);
        Set<Field> declaredFields = getDeclaredForInheritanceFields(clazz);

        Set<Field> inheritedAndNonOverrideFields = accessibleFields.stream()
                .filter(accessibleField -> declaredFields.stream()
                        .noneMatch(declaredField -> equalFields(accessibleField, declaredField)))
                .collect(Collectors.toUnmodifiableSet());

        classMetrics.setNumOfAccessibleFields(accessibleFields.size());
        classMetrics.setNumOfInheritedAndNonOverrideFields(inheritedAndNonOverrideFields.size());
    }

    private void calculateAttributeHidingCoefficients(ClassMetrics classMetrics) {
        Class<?> clazz = classMetrics.getClazz();

        Set<Field> privateFields = getPrivateFields(clazz);
        Set<Field> allDeclaredFields = getAllDeclaredFields(clazz);

        classMetrics.setNumOfPrivateFields(privateFields.size());
        classMetrics.setNumOfFields(allDeclaredFields.size());
    }

    private void calculatePolymorphismObjectCoefficients(ClassMetrics classMetrics) {
        Class<?> clazz = classMetrics.getClazz();

        Set<Method> inheritedAndOverriddenMethods = getInheritedAndOverriddenMethods(clazz);
        Set<Method> newMethods = getDeclaredMethods(clazz).stream()
                .filter(m1 -> inheritedAndOverriddenMethods.stream()
                        .noneMatch(m2 -> equalMethods(m1, m2)))
                .collect(Collectors.toUnmodifiableSet());

        classMetrics.setNumOfInheritedAndOverrideMethods(inheritedAndOverriddenMethods.size());
        classMetrics.setNumOfNewMethods(newMethods.size());
    }

    private Set<Class<?>> getAllClasses() {
        Set<String> classNames = reflections.getAllTypes();
        Set<Class<?>> classes = new HashSet<>();
        classNames.forEach(className -> {
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                System.err.println("Cannot load class:" + className);
            }
        });
        return classes;
    }

    private int calculateNumberOfChildren(Class<?> clazz) {
        return reflections.getStore().get(SubTypesScanner.class, clazz.getName()).size();
    }

}
