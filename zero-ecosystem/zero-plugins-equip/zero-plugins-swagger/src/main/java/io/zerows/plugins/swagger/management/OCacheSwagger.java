package io.zerows.plugins.swagger.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.management.OCache;
import io.zerows.plugins.swagger.SwaggerData;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Swagger 缓存接口，用于存储 Swagger 相关的数据
 *
 * @author lang : 2025-10-17
 */
public interface OCacheSwagger extends OCache<SwaggerData> {
    Cc<String, OCacheSwagger> CC_SKELETON = Cc.open();

    static OCacheSwagger of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheSwaggerAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheSwaggerAmbiguity(bundle), cacheKey);
    }

    static OCacheSwagger of() {
        return of(null);
    }

    /**
     * 获取所有 Bundle 中的 Swagger 数据
     *
     * @return 所有 Swagger 数据的集合
     */
    static Set<SwaggerData> entireValue() {
        return CC_SKELETON.values().stream()
            .map(OCache::value)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * 获取第一个可用的 Swagger 数据（通常全局只有一个）
     *
     * @return Swagger 数据，如果没有则返回 null
     */
    static SwaggerData entireFirst() {
        return CC_SKELETON.values().stream()
            .map(OCache::value)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}

