package com.lambda;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionExample {

    public static void main(String[] args) {
        Collection<Integer> collection = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            collection.add(i);
        }
        collection.removeIf(integer -> integer < 90);
        collection.forEach(p -> System.out.println(p));
        collection.stream().map(integer -> integer * 2).reduce((integer, integer2) -> integer - 1);
    }
}
