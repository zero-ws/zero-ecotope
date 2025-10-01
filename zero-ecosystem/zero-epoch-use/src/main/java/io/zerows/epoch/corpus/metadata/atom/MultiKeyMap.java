package io.zerows.epoch.corpus.metadata.atom;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多键哈希表，用来存储常用的多键结构（后期将会有大用，主要用于增强系统柔性），让整个系统可以直接面向人工和机器的核心数据结构
 * <pre><code>
 *        key = T
 *        name = T
 *        code = T
 *     上述结构中哈希表的键是多形态，可支持各种不同的字符串，只要可以区别就可直接使用
 * </code></pre>
 *
 * @author lang : 2024-05-12
 */
public class MultiKeyMap<T> {
    // key = T
    private final ConcurrentMap<String, T> storedMap = new ConcurrentHashMap<>();
    // field1 = key, field2 = key, field3 = key ...
    private final ConcurrentMap<String, String> vector = new ConcurrentHashMap<>();

    public T put(final String key, final T value, final String... fields) {
        this.storedMap.put(key, value);
        Arrays.stream(fields)
            .filter(Objects::nonNull)
            .forEach(field -> this.vector.put(field, key));
        return value;
    }

    public T remove(final String key) {
        final T value = this.storedMap.remove(key);
        this.vector.entrySet()
            .removeIf(entry -> entry.getValue().equals(key));
        return value;
    }

    public void clear() {
        this.storedMap.clear();
        this.vector.clear();
    }

    public T get(final String key) {
        return this.storedMap.get(key);
    }

    public Set<T> values() {
        return Set.copyOf(this.storedMap.values());
    }

    public T getOr(final String field) {
        if (this.vector.containsKey(field)) {
            final String key = this.vector.get(field);
            return this.storedMap.get(key);
        } else {
            return this.storedMap.get(field);
        }
    }

    public Set<String> keySet() {
        return this.storedMap.keySet();
    }
}
