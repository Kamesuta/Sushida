package net.teamfruit.sushida.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomCollectors {
    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            list -> {
                Collections.shuffle(list);
                return list;
            }
    );

    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, List<T>> toShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }

    public static <T> Collector<T, ?, List<T>> toRandomPickList(int count) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new),
                list -> {
                    if (count >= list.size())
                        return Collections.emptyList();
                    Random rand = ThreadLocalRandom.current();
                    IntStream.range(0, list.size() - count).forEach(e -> list.remove(rand.nextInt(list.size())));
                    return list;
                }
        );
    }

    public static List<Integer> splitInt(int amount, int n) {
        return IntStream.range(0, n).mapToObj(i -> (amount + i) / n).collect(Collectors.toList());
    }
}
