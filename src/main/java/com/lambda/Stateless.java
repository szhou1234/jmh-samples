package com.lambda;

// http://stackoverflow.com/questions/23983832/is-method-reference-caching-a-good-idea-in-java-8/23991339#23991339
public class Stateless {

    public static void main(String[] args) {
        stateless();
        stateful();
        unknown();
    }

    public static void stateless() {
        Runnable r1 = null;
        for (int i = 0; i < 2; i++) {
            Runnable r2 = System::gc;
            if (r1 == null) r1 = r2;
            else System.out.println(r1 == r2 ? "shared" : "unshared");
        }
    }

    public static void stateful() {
        Runnable r1 = null;
        for (int i = 0; i < 2; i++) {
            Runnable r2 = Runtime.getRuntime()::gc;
            if (r1 == null) {
                r1 = r2;
            } else {
                System.out.println(r1 == r2 ? "shared" : "unshared");
                System.out.println(r1.getClass() == r2.getClass() ? "shared class" : "unshared class");
            }
        }
    }

    public static void unknown() {
        Runnable r1 = System::gc, r2 = System::gc;
        System.out.println(r1 == r2 ? "shared" : "unshared");
        System.out.println(r1.getClass() == r2.getClass() ? "shared class" : "unshared class");
    }
}
