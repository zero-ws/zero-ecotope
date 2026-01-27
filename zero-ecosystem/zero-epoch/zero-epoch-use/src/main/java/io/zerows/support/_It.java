package io.zerows.support;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lang : 2023-06-19
 */
class _It extends _Ir {
    /*
     * Interator method for different usage
     *  1) itMap
     *  2) itSet
     *  3) itDay
     *  4) itWeek
     *  5) itList
     *  6) itArray
     *  7) itMatrix
     *  8) itCollection
     *  9) itRepeat
     * 10) itJObject
     * 11) itJArray
     * 12) itJson ( For <Tool> extract by JsonObject/JsonArray )
     * 13) itJString
     * 14) itStart / itEnd / itPart
     *
     * `it` means iterator method here
     * `et` means `Error Iterator` to be sure comsumer should throw some checked exception
     * The previous `et` have been moved to `FnZero` class instead, in Ut there are only `it` left
     */
    public static <K, V> void itMap(final ConcurrentMap<K, V> map, final BiConsumer<K, V> fnEach) {
        CollectionIt.exec(map, fnEach);
    }

    public static <V> void itSet(final Set<V> set, final BiConsumer<V, Integer> fnEach) {
        final List<V> list = new ArrayList<>(set);
        CollectionIt.exec(list, fnEach);
    }

    public static <V> void itList(final List<V> list, final BiConsumer<V, Integer> fnEach) {
        CollectionIt.exec(list, fnEach);
    }


    public static <V> void itArray(final V[] array, final BiConsumer<V, Integer> fnEach) {
        CollectionIt.exec(Arrays.asList(array), fnEach);
    }

    public static <V> void itMatrix(final V[][] array, final Consumer<V> fnEach) {
        CollectionIt.exec(array, fnEach);
    }

    public static <F, S> void itCollection(final java.util.Collection<F> firsts, final Function<F, Collection<S>> seconds, final BiConsumer<F, S> consumer) {
        CollectionIt.exec(firsts, seconds, consumer);
    }

    public static <F, S> void itCollection(final java.util.Collection<F> firsts, final Function<F, java.util.Collection<S>> seconds, final BiConsumer<F, S> consumer, final BiPredicate<F, S> predicate) {
        CollectionIt.exec(firsts, seconds, consumer, predicate);
    }

    public static <T> void itStart(final JsonObject data, final String prefix, final BiConsumer<T, String> consumer) {
        CollectionIt.exec(data, prefix, true, consumer);
    }

    public static <T> void itPart(final JsonObject data, final String prefix, final BiConsumer<T, String> consumer) {
        CollectionIt.exec(data, prefix, null, consumer);
    }

    public static <T> void itEnd(final JsonObject data, final String prefix, final BiConsumer<T, String> consumer) {
        CollectionIt.exec(data, prefix, false, consumer);
    }

}
