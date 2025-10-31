package io.zerows.epoch.management;

import io.zerows.epoch.basicore.MDMeta;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-08
 */
@Slf4j
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
