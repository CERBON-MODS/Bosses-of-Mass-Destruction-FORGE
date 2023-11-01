package com.cerbon.bosses_of_mass_destruction.util;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class CollectionUtils {
    /**
     * Accumulates value starting with [initial] value and applying [operation] from left to right
     * to current accumulator value and each element.
     *
     * Returns the specified [initial] value if the collection is empty.
     *
     * @param operation function that takes current accumulator value and an element, and calculates the next accumulator value.
     */
    public static <T, R> R fold(Iterable<T> iterable, R initial, BiFunction<R, T, R> operation) {
        R accumulator = initial;
        for (T element : iterable) {
            accumulator = operation.apply(accumulator, element);
        }
        return accumulator;
    }

    /**
     * Returns a list of pairs of each two adjacent elements in this collection.
     *
     * The returned list is empty if this collection contains less than two elements.
     *
     */
    public static <T> List<Pair<T, T>> zipWithNext(List<T> iterable){
        return zipWithNext(iterable, Pair::of);
    }

    /**
     * Returns a list containing the results of applying the given [transform] function
     * to an each pair of two adjacent elements in this collection.
     *
     * The returned list is empty if this collection contains less than two elements.
     *
     */
    public static <T, R> List<R> zipWithNext(Iterable<T> iterable, BiFunction<T, T, R> transform) {
        Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext()) return List.of();
        List<R> result = new ArrayList<>();
        T current = iterator.next();
        while (iterator.hasNext()) {
            T next = iterator.next();
            result.add(transform.apply(current, next));
            current = next;
        }
        return result;
    }
}
