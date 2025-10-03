package io.zerows.epoch.corpus.metadata.service;

import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.sdk.osgi.ServiceContext;
import io.zerows.epoch.sdk.osgi.ServiceInvocation;
import io.zerows.specification.configuration.HSetting;
import org.apache.felix.dm.DependencyManager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 服务参数
 * <pre><code>
 *     1. 模块配置
 *        1.1. 入口配置 {@link HSetting}
 *        /vertx.yml
 *        /vertx-xxx.yml
 *        ...
 *        1.2. 模块配置 {@link MDConfiguration}
 *     2. 依赖配置器 {@link DependencyManager}
 *     3. 调用处理器
 *        3.1. 服务调用 {@link ServiceInvocation} 提供者列表
 *        3.2. 服务调用 / 消费者清单
 * </code></pre>
 *
 * @author lang : 2024-07-01
 */
public class CallbackParameter implements Serializable {

    private final ServiceContext context;
    private final Set<ServiceInvocation> providers = new HashSet<>();
    private final Set<String> consumers = new HashSet<>();
    private final DependencyManager dependency;

    public CallbackParameter(final ServiceContext context,
                             final DependencyManager dependency) {
        this.context = context;
        this.dependency = dependency;
    }

    // ---------------- 拥有者引用 ----------------

    public DependencyManager dependency() {
        return this.dependency;
    }

    public ServiceContext context() {
        return this.context;
    }


    // ---------------- 服务调用链 ----------------
    public CallbackParameter providers(final ServiceInvocation... providers) {
        this.providers.addAll(Arrays.asList(providers));
        return this;
    }

    public Set<ServiceInvocation> providers() {
        return this.providers;
    }

    public boolean isProvider() {
        return !this.providers.isEmpty();
    }

    public CallbackParameter consumers(final String... consumers) {
        this.consumers.addAll(Arrays.asList(consumers));
        return this;
    }

    public Set<String> consumers() {
        return this.consumers;
    }

    public boolean isConsumer() {
        return !this.consumers.isEmpty();
    }
}
