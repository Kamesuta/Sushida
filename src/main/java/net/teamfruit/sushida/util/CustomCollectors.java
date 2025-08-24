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

    /**
     * Streamの要素から、指定された個数になるまで要素をランダムに抽出します。
     * <p>
     * この処理は、トランプの山札からカードを引く動作に似ています。
     * 元のリスト（山札）をシャッフルし、全ての要素を一度ずつ使います。
     * 要求された個数に満たない場合、再び元のリストをシャッフルし、足りない分の要素を追加します。
     * <p>
     * <b>例:</b>
     * 元のリストが {@code [A, B, C]} で、5個の要素を要求された場合
     * <ol>
     *     <li>リストをシャッフルし、3要素を取得します (例: {@code [C, A, B]})。</li>
     *     <li>まだ2要素足りないため、再度リストをシャッフルし、先頭から2要素を取得します (例: {@code [B, A]})。</li>
     *     <li>最終的に、これらを連結したリスト (例: {@code [C, A, B, B, A]}) を返します。</li>
     * </ol>
     *
     * @param count 最終的に取得したい要素の数
     * @param <T>   要素の型
     * @return 指定された個数の要素を持つリストを生成する {@link Collector}
     */
    public static <T> Collector<T, ?, List<T>> toCyclingShuffledList(int count) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new),
                list -> {
                    if (list.isEmpty() || count <= 0) {
                        return Collections.emptyList();
                    }

                    List<T> resultList = new ArrayList<>(count);
                    List<T> tempList = new ArrayList<>(list);

                    int listSize = list.size();
                    int numCycles = count / listSize;
                    for (int i = 0; i < numCycles; i++) {
                        Collections.shuffle(tempList);
                        resultList.addAll(tempList);
                    }

                    int remaining = count % listSize;
                    if (remaining > 0) {
                        Collections.shuffle(tempList);
                        resultList.addAll(tempList.subList(0, remaining));
                    }

                    return resultList;
                }
        );
    }

    public static List<Integer> splitInt(int amount, int n) {
        return IntStream.range(0, n).mapToObj(i -> (amount + i) / n).collect(Collectors.toList());
    }
}
