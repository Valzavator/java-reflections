package com.maxsvynarchuk.test.methods;

public interface Interface2 extends Interface1 {

    @Override
    default void defaultMethod1() {

    }

    default void defaultMethod2() {

    }

    private void privateInterfaceMethod() {
    }
}
