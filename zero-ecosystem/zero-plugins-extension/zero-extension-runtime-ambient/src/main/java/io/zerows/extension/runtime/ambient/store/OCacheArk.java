package io.zerows.extension.runtime.ambient.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.support.Ut;
import io.zerows.sdk.management.OCache;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.util.Objects;
import java.util.Set;

/**
 * 内置存储器，上层对接 RegistryExtension 的注册器，用于从环境中提取所有的应用程序专用，应用是双设计
 * <pre><code>
 *     1. {@link HArk} 带详细配置的应用程序相关信息
 *     2. {@link HApp} 内置专用应用程序信息（目前是 HApp x 1）
 * </code></pre>
 *
 * @author lang : 2024-07-08
 */
public interface OCacheArk extends OCache<HArk> {

    Cc<String, OCacheArk> CC_SKELETON = Cc.open();

    static OCacheArk of(final Bundle owner) {
        final String cacheKey = Ut.Bnd.keyCache(owner, OCacheArkAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheArkAmbiguity(owner), cacheKey);
    }

    static OCacheArk of() {
        final Bundle owner = FrameworkUtil.getBundle(OCacheArk.class);
        return of(owner);
    }

    default OCacheArk add(final Set<HArk> arkSet) {
        arkSet.stream().filter(Objects::nonNull).forEach(this::add);
        return this;
    }

    default OCacheArk remove(final Set<HArk> arkSet) {
        arkSet.stream().filter(Objects::nonNull).forEach(this::remove);
        return this;
    }
}
