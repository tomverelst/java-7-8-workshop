package be.ordina.workshop.java8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Yannick De Turck
 */
public class StreamExamples {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 3, 5, 7, 13, 17, 23);

        // Filter
        list.stream()
                .filter(i -> i > 10)
                .forEach(System.out::println);

        System.out.println("---");

        // Sorted
        list.stream()
                .sorted((i1, i2) -> i1.compareTo(i2) * -1)
                .forEach(System.out::println);

        System.out.println("---");

        // Map
        list.stream()
                .map(i -> i + 1)
                .forEach(System.out::println);

        System.out.println("---");

        // Collect
        String joinedList = list.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(", "));
        System.out.println(joinedList);

        System.out.println("---");

        // Match
        boolean allArePrimeNumbers = list.stream().allMatch(i -> {
            for(int u=2; i<u; u++) {
                if(i % u == 0) {
                    return false;
                }
            }
            return true;
        });

        System.out.println("Are all numbers prime numbers? " + (allArePrimeNumbers ? "Yes" : "Nope"));

        System.out.println("---");

        // Count
        long amountSmallerThan10 = list.stream()
                .filter(i -> i < 10)
                .count();
        System.out.println("Amount smaller than 10: " + amountSmallerThan10);

        System.out.println("---");

        // Reduce
        Optional<Integer> reduced = list.stream()
                .reduce((i1, i2) -> i1 + i2);
        reduced.ifPresent(i -> System.out.println(i));

        System.out.println("---");

        // Optional
        Optional<String> optional = Optional.of("value");
        Optional<String> emptyOptional = Optional.empty();
        Optional<Integer> length = optional.map(String::length);
        System.out.println("length = " + length.orElse(0));

        optional.map(s -> s.substring(0, 3))
            .filter(t -> t.contains("al"))
            .ifPresent(System.out::println);

        System.out.println("---");
    }
}
