package io.zerows.epoch.management;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.zerows.epoch.basicore.MDMeta;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-08
 */
class OCacheDaoAmbiguity extends AbstractAmbiguity implements OCacheDao {
    /**
     * Norm 环境中，OCacheDao 内部会维护一个单独的 Map，这个 Map 就是独立的配置信息，此时构造的 OCacheDao 等价于一个单件模式，使用了
     * 当前类名来构造对应的访问信息。
     */
    private final ConcurrentMap<String, MDMeta> storedMeta = new ConcurrentHashMap<>();

    OCacheDaoAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<String> keys() {
        return this.storedMeta.keySet();
    }

    @Override
    public MDMeta valueGet(final String key) {
        return this.storedMeta.get(key);
    }

    @Override
    public MDMeta keyGet(final Class<?> daoCls) {
        return this.storedMeta.values().stream()
            .filter(meta -> daoCls.equals(meta.dao()))
            .findAny().orElse(null);
    }

    @Override
    public OCacheDao add(final MDMeta pojo) {
        Objects.requireNonNull(pojo);
        this.storedMeta.put(pojo.table(), pojo);
        return this;
    }

    @Override
    public OCacheDao remove(final MDMeta pojo) {
        this.storedMeta.remove(pojo.table());
        return this;
    }

    /**
     * 这个过程只执行一次，不同环境执行效果会不相同
     * <pre><code>
     *     1. 原始环境中，由于每个 Module 都可能调用，但读取到的对象在上层 SKELETON 中只会出现一次，所以第二次调用时直接就返回了
     *     2. OSGI 环境中，每个 Bundle 都会调用一次，所以每个 Bundle 都会有一份自己的配置
     * </code></pre>
     *
     * @param configuration 配置对象
     * @param <C>           泛型
     *
     * @return OCacheDao
     */
    @Override
    public <C> OCacheDao configure(final C configuration) {
        if (!this.storedMeta.isEmpty()) {
            return this;
        }
        // 扫描的类，环境构造时直接扫描
        final Set<Class<?>> scanned = OCacheClass.of(this.caller()).value();
        final ConcurrentMap<String, Class<?>> daoMap = new ConcurrentHashMap<>();
        scanned.stream()
            .filter(AbstractVertxDAO.class::isAssignableFrom)
            .forEach(daoCls -> daoMap.put(MDMeta.toTable(daoCls), daoCls));

        final ConcurrentMap<String, Class<?>> pojoMap = new ConcurrentHashMap<>();
        scanned.stream()
            .filter(VertxPojo.class::isAssignableFrom)
            .forEach(pojoCls -> pojoMap.put(MDMeta.toTable(pojoCls), pojoCls));

        final Set<String> lineSet = new TreeSet<>();
        final ConcurrentMap<String, String> lineMap = new ConcurrentHashMap<>();
        daoMap.forEach((table, daoCls) -> {
            final Class<?> pojoCls = pojoMap.getOrDefault(table, null);
            if (Objects.nonNull(pojoCls)) {
                final MDMeta pojo = new MDMeta(daoCls, pojoCls);
                this.add(pojo);
                lineMap.put(pojo.table(), pojo.toLine());
                lineSet.add(pojo.table());
            }
        });
        // 打印结果
        final List<String> lines = new ArrayList<>();
        lineSet.forEach(table -> lines.add(lineMap.get(table)));
        if (lines.isEmpty()) {
            return this;
        }

        if (Objects.isNull(this.caller())) {
            this.logger().info("Scanned \"{}\" table with Dao configuration in Norm environment. \n{}",
                this.storedMeta.size(), Ut.fromJoin(lines, "\n"));
        } else {
            this.logger().info("Scanned \"{}\" table with Dao configuration in OSGI environment. Bundle = {}, \n{}",
                this.storedMeta.size(), this.caller().name(), Ut.fromJoin(lines, "\n"));
        }

        // 打印结果
        return this;
    }

    @Override
    public OCacheDao add(final Set<MDMeta> daoSet) {
        daoSet.forEach(this::add);
        return this;
    }

    @Override
    public OCacheDao remove(final Set<MDMeta> daoSet) {
        daoSet.forEach(this::remove);
        return this;
    }
}
