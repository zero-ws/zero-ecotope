package io.zerows.platform.metadata;

import io.zerows.platform.constant.VName;
import io.zerows.platform.enums.EmApp;
import io.zerows.support.base.UtBase;
import io.zerows.specification.access.HBelong;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.vital.HOI;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-07
 */
class _KAmbientRuntime {
    private static final ConcurrentMap<String, String> VECTOR = new ConcurrentHashMap<>();

    /**
     * 替换原始的 HES / HET / HOI 专用
     * <pre><code>
     *     构造向量表
     *     name
     *     ns           = cacheKey
     *     appId
     *     appKey       = cacheKey
     *     tenantId     = cacheKey
     *     sigma        = cacheKey
     * </code></pre>
     *
     * @param ark  HArk 应用配置容器
     * @param mode EmApp.Mode 应用模式
     */
    void registry(final HArk ark, final EmApp.Mode mode) {
        // 提取缓存键和应用程序引用
        final String key = UtBase.keyApp(ark);
        final HApp app = ark.app();

        // 1. 基础规范：name / ns
        final String ns = app.ns();
        VECTOR.put(ns, key);
        final String name = app.name();
        VECTOR.put(name, key);

        // 2. 扩展规范：code / appKey / appId
        final String code = app.option(VName.CODE);
        Optional.ofNullable(code).ifPresent(each -> VECTOR.put(each, key));
        final String appId = app.option(VName.APP_ID);
        Optional.ofNullable(appId).ifPresent(each -> VECTOR.put(each, key));
        final String appKey = app.option(VName.APP_KEY);
        Optional.ofNullable(appKey).ifPresent(each -> VECTOR.put(each, key));

        // 3. 选择规范：sigma / tenantId
        if (EmApp.Mode.CUBE == mode) {
            // 单租户 / 单应用，tenantId / sigma 可表示缓存键（唯一的情况）
            final HOI hoi = ark.owner();
            Optional.ofNullable(hoi).map(HBelong::owner).ifPresent(each -> VECTOR.put(each, key));
            final String sigma = app.option(VName.SIGMA);
            Optional.ofNullable(sigma).ifPresent(each -> VECTOR.put(each, key));
        }
    }

    String keyFind(final String key) {
        return VECTOR.getOrDefault(key, null);
    }
}
