package com.cassandra;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchTest {

    public static void main(String[] args) throws InterruptedException {
        testLatch();
    }

    public static void testLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        latch.countDown();

        new Thread(() -> {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                System.err.println("interrupted");
            }
            latch.countDown();
            System.out.println("counted down");
        }).start();


        latch.await(1, TimeUnit.SECONDS);
        if (latch.getCount() > 0) {
            System.err.println("failed");
        } else {
            System.out.println("success");
        }
    }
}
