package org.clover;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class App {
    private static final int numLines = Runtime.getRuntime().availableProcessors();
    private static final Map<String, String> lastOutputPerProcess = new HashMap<>();
    private static final Object lock = new Object();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(numLines);
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            final String repoName = "repo" + letter;
            executor.submit(() -> processRepo(repoName));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void processRepo(String repoName) {
        log(repoName, "Starting");
        randSleep();
        log(repoName, "Installing");
        randSleep();
        log(repoName, "Building");
        randSleep();
        log(repoName, "Instrumenting");
        randSleep();
        log(repoName, "Running tests");
        randSleep();
        log(repoName, "Result in " + repoName + ".json");
        synchronized (lock) {
            lastOutputPerProcess.remove(repoName);
            cleanUp();
        }
    }

    private static void log(String repoName, String message) {
        synchronized (lock) {
            lastOutputPerProcess.put(repoName, message);
            cleanUp();
            lastOutputPerProcess.forEach((name, msg) -> System.out.println(name + ": " + msg));
            System.out.flush();
        }
    }

    private static void randSleep() {
        try {
            Thread.sleep(new Random().nextInt(2000) + 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void cleanUp() {
        // Terminal control to clean up the console is limited in Java and varies by platform and terminal.
        // This is a simple placeholder. You might need to implement more complex logic for your specific environment.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
