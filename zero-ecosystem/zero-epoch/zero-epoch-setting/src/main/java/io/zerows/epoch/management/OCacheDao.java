package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.MDMeta;
import io.zerows.sdk.management.OCache;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 底层基于Jooq的 Class<?> 提取，最终分析会反应到数据表上，主要会被 MDEntity 调用来构造全局数据表中的类信息
 *
 * @author lang : 2024-05-08
 */
public interface OCacheDao extends OCache<MDMeta> {
    Cc<String, OCacheDao> CC_SKELETON = Cc.open();

    static OCacheDao of(final Bundle bundle) {
        return CC_SKELETON.pick(() -> new OCacheDaoAmbiguity(bundle),
            Ut.Bnd.keyCache(bundle, OCacheDaoAmbiguity.class));
    }

    static OCacheDao of() {
        return of(null);
    }

    static Set<String> entireKeys() {
        return CC_SKELETON.get().values().stream()
            .flatMap(item -> item.keys().stream())
            .collect(Collectors.toSet());
    }

    static MDMeta entireMeta(final String table) {
        return CC_SKELETON.get().values().stream()
            .map(item -> item.valueGet(table))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

    static MDMeta entireMeta(final Class<?> daoCls) {
        return CC_SKELETON.get().values().stream()
            .map(item -> item.keyGet(daoCls))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

    static Class<?> findDao(final String daoOrTable) {
        final MDMeta meta = entireMeta(daoOrTable);
        if (Objects.isNull(meta)) {
            // 通过表名无法查找到对应的 Meta，尝试执行类名查找
            return Ut.clazz(daoOrTable, null);
        } else {
            // 如果通过表名找到了对应的 Dao，则直接提取 Dao 信息
            return meta.dao();
        }
    }

    static MDMeta findMeta(final String daoOrTable) {
        final MDMeta meta = entireMeta(daoOrTable);
        if (Objects.isNull(meta)) {
            // 无法找到对应的 meta，尝试深度检索（通过 Dao 查找）
            final Class<?> daoCls = Ut.clazz(daoOrTable, null);
            if (Objects.isNull(daoCls)) {
                return null;
            }
            return entireMeta(daoCls);
        } else {
            return meta;
        }
    }

    MDMeta keyGet(Class<?> daoCls);

    OCacheDao add(Set<MDMeta> daoSet);

    OCacheDao remove(Set<MDMeta> daoSet);
}
