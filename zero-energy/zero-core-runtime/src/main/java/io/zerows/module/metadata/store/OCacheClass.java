package io.zerows.module.metadata.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.constant.KMeta;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.zdk.running.OCache;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-04-19
 */
public interface OCacheClass extends OCache<Set<Class<?>>> {

    Cc<String, OCacheClass> CC_SKELETON = Cc.open();

    static OCacheClass of() {
        return of(null);
    }

    static OCacheClass of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheClassAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheClassAmbiguity(bundle), cacheKey);
    }

    // ------------------ 全局方法 ------------------
    static Set<Class<?>> entireValue() {
        return OCacheClassAmbiguity.META().stream()
            .flatMap(meta -> meta.get().stream())
            .collect(Collectors.toSet());
    }

    static KMeta.Typed entireType(final Class<?> clazz) {
        return OCacheClassAmbiguity.META().stream()
            .map(meta -> meta.getType(clazz))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

    static Set<Class<?>> entireValue(final KMeta.Typed type) {
        return OCacheClassAmbiguity.META().stream()
            .flatMap(meta -> meta.get(type).stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }


    // ------------------ 实例方法 ------------------
    OCacheClass compile(KMeta.Typed type, Function<Set<Class<?>>, Set<Class<?>>> compiler);

    Set<Class<?>> value(KMeta.Typed type);

    KMeta.Typed valueType(Class<?> clazz);
}
