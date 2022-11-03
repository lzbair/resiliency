package io.patterns.circuitbreaker;

import java.util.Random;

public class RemoteApi {

    public static final Random RANDOM = new Random();

    public String probe() throws InterruptedException {
        if (RANDOM.nextBoolean()) {
            Thread.sleep(5000);
        }
        return "Result ----> ok";
    }

    public String fallback() {
        return "Fallback: Result ----> good enough";
    }
}
