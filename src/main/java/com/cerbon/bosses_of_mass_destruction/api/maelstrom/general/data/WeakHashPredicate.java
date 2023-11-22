package com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data;

import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class WeakHashPredicate<T> implements Predicate<T> {
    private final Supplier<Supplier<Boolean>> predicateFactory;
    private final WeakHashMap<T, Supplier<Boolean>> conditionals = new WeakHashMap<>();

    public WeakHashPredicate(Supplier<Supplier<Boolean>> predicateFactory) {
        this.predicateFactory = predicateFactory;
    }

    @Override
    public boolean test(T t) {
        if (!conditionals.containsKey(t))
            conditionals.put(t, predicateFactory.get());

        return conditionals.get(t).get();
    }
}

