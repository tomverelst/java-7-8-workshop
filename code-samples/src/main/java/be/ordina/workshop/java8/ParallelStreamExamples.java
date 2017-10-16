package be.ordina.workshop.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yannick De Turck
 */
public class ParallelStreamExamples {
    public static void main(String[] args) {
        // Create a big list
        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i=0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        // Compare sequential sort vs parallel sort
        // Sequential
        long timeBefore = System.nanoTime();
        final List<String> sequential = values.stream().sorted().collect(Collectors.toList());
        long timeAfter = System.nanoTime();
        long totalTime = TimeUnit.NANOSECONDS.toMillis(timeAfter - timeBefore);
        System.out.println(String.format("First item of sequential sort: %s", sequential.get(0)));
        System.out.println(String.format("Sequential sort took: %d ms.", totalTime));

        // Parallel
        timeBefore = System.nanoTime();
        final List<String> parallel = values.parallelStream().sorted().collect(Collectors.toList());
        timeAfter = System.nanoTime();
        totalTime = TimeUnit.NANOSECONDS.toMillis(timeAfter - timeBefore);
        System.out.println(String.format("First item of parallel sort: %s", parallel.get(0)));
        System.out.println(String.format("parallel sort took: %d ms.", totalTime));
    }
}
