package com.maxsvynarchuk.test2;

public interface Interface2 extends Interface1 {

    default void def2() {}

    @Override
    default void def1() {

    }
}
