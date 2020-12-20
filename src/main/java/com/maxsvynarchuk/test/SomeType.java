package com.maxsvynarchuk.test;

public abstract class SomeType  implements Interface2 {

    abstract void abstractM();

    public void a(int a, int b) {
        System.out.println("A");
    }

    public static void staticM() {}

//    int b(int a) {
//        return a;
//    }

    private void c() {}

}
