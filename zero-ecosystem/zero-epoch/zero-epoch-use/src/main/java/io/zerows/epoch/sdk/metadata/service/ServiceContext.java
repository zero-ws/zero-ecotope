package io.zerows.epoch.sdk.metadata.service;

import io.r2mo.typed.cc.Cc;
import io.zerows.component.log.OLog;
import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.metadata.service.context.ContextOfApp;
import io.zerows.epoch.corpus.metadata.service.context.ContextOfModule;
import io.zerows.epoch.corpus.metadata.service.context.ContextOfPlugin;
import io.zerows.enums.EmService;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.dependency.OCallback;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

/**
 * 整体服务上下文结构
 * <pre><code>
 *     {@link ContextOfPlugin} 插件上下文
 *     - 只包含 Bundle、上下文哈希表
 *     {@link ContextOfModule} 模块上下文
 *     - 在插件上下文基础上多一层 {@link MDConfiguration}
 *     {@link ContextOfApp} 应用上下文
 *     - 在模块上下文基础上多一层 {@link HSetting}
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public interface ServiceContext {

    Cc<String, ServiceContext> CC_SKELETON = Cc.open();

    static ServiceContext ofPlugin(final Bundle owner) {
        final String cacheKey = Ut.Bnd.keyCache(owner, ContextOfPlugin.class);
        return CC_SKELETON.pick(() -> new ContextOfPlugin(owner), cacheKey);
    }

    static ServiceContext ofApp(final Bundle owner) {
        final String cacheKey = Ut.Bnd.keyCache(owner, ContextOfApp.class);
        return CC_SKELETON.pick(() -> new ContextOfApp(owner), cacheKey);
    }

    static ServiceContext ofModule(final MDConfiguration configuration) {
        final Bundle owner = configuration.id().owner();
        final String cacheKey = Ut.Bnd.keyCache(owner, ContextOfModule.class);
        return CC_SKELETON.pick(() -> new ContextOfModule(owner).putConfiguration(configuration), cacheKey);
    }

    default EmService.Context type() {
        return EmService.Context.PLUGIN;
    }

    default Bundle owner() {
        return null;
    }

    // 配置接口
    default HSetting setting() {
        return null;
    }

    default MDConfiguration configuration() {
        return null;
    }

    default ServiceContext putConfiguration(final MDConfiguration configuration) {
        return this;
    }

    // 上下文专用处理
    ServiceContext put(String field, Object value);

    ServiceContext putOr(String field, Object value);

    ServiceContext remove(String field);

    <T> T get(String field);

    <T> T getOr(String field);

    default OLog logger() {
        return Ut.Log.service(this.getClass());
    }

    default Class<?> signalClass() {
        return null;
    }

    interface OK extends OCallback.Signal {
    }
}
