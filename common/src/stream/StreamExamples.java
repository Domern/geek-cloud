package stream;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class StreamExamples {
    public static void main(String[] args) {
        IntStream.range(0, 10)
                .boxed()
                .filter(x -> x % 2 == 1)
                .forEach(x -> System.out.print(x + " "));
        System.out.println();


        List<Integer> odds = IntStream.range(0, 10)
                .boxed()
                .filter(x -> x % 2 == 1)
                .toList();
        System.out.println(odds);


        boolean allMatch = IntStream.range(0, 10)
                .boxed()
                .filter(x -> x % 2 == 1)
                .allMatch(x -> x > 10 && x > 10);
        System.out.println(allMatch);

        IntStream.range(0, 10)
                .boxed()
                .filter(x -> x % 2 == 1)
                .max(Comparator.comparingInt(x -> x))
                .ifPresent(System.out::println);

        Integer sum = IntStream.range(0, 10)
                .boxed()
                .reduce(0, Integer::sum);
        System.out.println(sum);


    }
}
