package io.zerows.platform.metadata;


import io.zerows.support.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KVarSet implements Serializable {

    private final ConcurrentMap<String, KVar> attrMap = new ConcurrentHashMap<>();

    private KVarSet() {
    }

    public static KVarSet of(final ConcurrentMap<String, KVar> data) {
        final KVarSet set = new KVarSet();
        set.bind(data);
        return set;
    }

    public static KVarSet of() {
        return of(new ConcurrentHashMap<>());
    }

    public KVarSet bind(final ConcurrentMap<String, KVar> data) {
        if (Objects.nonNull(data)) {
            this.attrMap.putAll(data);
        }
        return this;
    }

    public KVarSet save(final String name, final String alias) {
        return this.saveWith(name, alias, String.class, null);
    }

    public KVarSet save(final String name, final String alias, final Class<?> type) {
        return this.saveWith(name, alias, type, null);
    }

    public KVarSet saveWith(final String name, final String alias,
                            final Object value) {
        return this.saveWith(name, alias, String.class, value);
    }

    public KVarSet saveWith(final String name, final String alias,
                            final Class<?> type, final Object value) {
        final KVar attr;
        if (this.attrMap.containsKey(name)) {
            attr = this.attrMap.get(name);
        } else {
            attr = KVar.of(name);
        }
        attr.bind(Objects.isNull(type) ? String.class : type);
        if (UtBase.isNotNil(alias)) {
            attr.bind(alias);
        }
        attr.value(value);
        this.attrMap.put(name, attr);
        return this;
    }

    public KVarSet remove(final String name) {
        this.attrMap.remove(name);
        return this;
    }

    public KVar attribute(final String name) {
        return this.attrMap.getOrDefault(name, null);
    }

    public Set<String> names() {
        return this.attrMap.keySet();
    }
}
