package com.szhou.lambda;

@FunctionalInterface
public interface FInterface {

    void foo();

    static void main(String[] args) {
        FInterface f = () -> System.out.println("good");
        f.foo();
    }
}
