package com.maxsvynarchuk.metric;

import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectionHelper {

    public static int calculateDepthOfInheritanceTree(Class<?> clazz) {
        if (Modifier.isInterface(clazz.getModifiers())) {
            return -1;
        }
        int depth = 0;
        while (Objects.nonNull(clazz.getSuperclass())) {
            clazz = clazz.getSuperclass();
            depth++;
        }
        return depth - 1;
    }

    public static Set<Method> getAccessibleMethods(Class<?> clazz) {
        Set<Method> allMethodsFromSupertypes = ReflectionUtils.getAllMethods(clazz, getMethodModifiersPredicate());
        Set<Method> accessibleMethods = new HashSet<>();
        for (Method m1 : allMethodsFromSupertypes) {
            if (accessibleMethods.stream().noneMatch(m2 -> equalMethods(m1, m2))) {
                accessibleMethods.add(m1);
            }
        }
        return accessibleMethods;
    }

    public static Set<Method> getDeclaredMethods(Class<?> clazz) {
        Set<Method> declaredMethods = ReflectionUtils.getMethods(clazz, getMethodModifiersPredicate());
        if (Modifier.isInterface(clazz.getModifiers())) {
            return Arrays.stream(clazz.getDeclaredMethods())
                    .filter(declaredMethods::contains)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return declaredMethods;
    }

    public static Set<Method> getInheritedAndOverriddenMethods(Class<?> clazz) {
        if (Objects.isNull(clazz.getSuperclass()) && clazz.getInterfaces().length == 0)
            return Collections.emptySet();

        Set<Method> allMethodsFromSupertypes = new HashSet<>();
        if (Objects.nonNull(clazz.getSuperclass())) {
            allMethodsFromSupertypes.addAll(getAccessibleMethods(clazz.getSuperclass()));
        }
        if (clazz.getInterfaces().length != 0) {
            for (Class<?> interfaceClass: clazz.getInterfaces()) {
                allMethodsFromSupertypes.addAll(getAccessibleMethods(interfaceClass));
            }
        }

        Set<Method> declaredMethods = getDeclaredMethods(clazz);
        return declaredMethods.stream()
                .filter(m1 -> allMethodsFromSupertypes.stream()
                        .anyMatch(m2 -> equalMethods(m1, m2)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Field> getAccessibleFields(Class<?> clazz) {
        Set<Field> allFieldsFromSupertypes = ReflectionUtils.getAllFields(clazz, getFieldModifiersPredicate());
        Set<Field> accessibleFields = new HashSet<>();
        for (Field f1 : allFieldsFromSupertypes) {
            if (accessibleFields.stream().noneMatch(f2 -> equalFields(f1, f2))) {
                accessibleFields.add(f1);
            }
        }
        return accessibleFields;
    }

    public static Set<Field> getDeclaredForInheritanceFields(Class<?> clazz) {
        return ReflectionUtils.getFields(clazz, getFieldModifiersPredicate());
    }

    public static Set<Method> getPrivateMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> Modifier.isPrivate(m.getModifiers()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Method> getOpenMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> !Modifier.isAbstract(m.getModifiers()) && !Modifier.isPrivate(m.getModifiers()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Field> getPrivateFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(m -> Modifier.isPrivate(m.getModifiers()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Field> getAllDeclaredFields(Class<?> clazz) {
        return Modifier.isInterface(clazz.getModifiers())
                ? Collections.emptySet()
                : Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toUnmodifiableSet());
    }

    public static boolean equalMethods(Method m1, Method m2) {
        if (m1 == null || m2 == null) return false;
        if (m1 == m2) return true;
        return m1.getName().equals(m2.getName())
                && Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes());
    }

    public static boolean equalFields(Field f1, Field f2) {
        if (f1 == null || f2 == null) return false;
        if (f1 == f2) return true;
        return f1.getName().equals(f2.getName());
    }

    public static Predicate<Method> getMethodModifiersPredicate() {
        return method -> {
            int mod = method.getModifiers();
            return !Modifier.isAbstract(mod) && !Modifier.isPrivate(mod) && !Modifier.isStatic(mod);
        };
    }

    public static Predicate<Field> getFieldModifiersPredicate() {
        return field -> {
            int mod = field.getModifiers();
            return !Modifier.isPrivate(mod) && !Modifier.isStatic(mod);
        };
    }

}
