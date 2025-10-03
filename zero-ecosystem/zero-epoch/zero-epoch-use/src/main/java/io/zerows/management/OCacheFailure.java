package io.zerows.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.sdk.management.OCache;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-28
 */
public interface OCacheFailure extends OCache<JsonObject> {

    Cc<String, OCacheFailure> CC_SKELETON = Cc.open();

    /**
     * 单独应用环境专用的组件键值，如果是单点应用环境（非 OSGI 模式），那么所有的 OCache 内置 Cc 架构之下的键值不根据 Bundle 执行计算，
     * 而只能是一个固定的值，这个值是 __NO_BUNDLE_，这个固定值在每种组件中都维持一致，简单结构如下：
     * <pre><code>
     *     1. 异常表：__NO_BUNDLE_ = ???
     *     2. 服务表：__NO_BUNDLE_ = ???
     *     3. 配置表：__NO_BUNDLE_ = ???
     * </code></pre>
     * 这种设计有利于 单机版 和 热插拔 版本二者同时处理的完美兼容性，并且方便开发者在两种模式之间切换，而且不需要修改任何代码，大概率运行
     * 模式只能二选一：要么是 OSGI 模式，要么是单机模式。
     */
    static OCacheFailure of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheFailureAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheFailureAmbiguity(bundle), cacheKey);
    }

    // ------------------ 全局方法 ------------------

    /**
     * 全局提取 error 节点数据
     *
     * @return JsonObject
     */
    static JsonObject entireError() {
        return Ut.valueJObject(OCacheFailureAmbiguity.GLOBAL_DATA, KName.ERROR, true);
    }

    /**
     * 全局提取 info 节点数据
     *
     * @return JsonObject
     */
    static JsonObject entireFailure() {
        return Ut.valueJObject(OCacheFailureAmbiguity.GLOBAL_DATA, KName.INFO, true);
    }
}
