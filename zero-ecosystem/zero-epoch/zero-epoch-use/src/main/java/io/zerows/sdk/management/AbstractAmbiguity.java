package io.zerows.sdk.management;

import io.zerows.specification.development.compiled.HBundle;

/**
 * 带有二义性的新组件，用于支持二选一的环境处理，其中包括
 * <pre><code>
 *     1. {@link HBundle} == null：OSGI 环境
 *     2. {@link HBundle} != null：OSGI 环境
 * </code></pre>
 * 其中此处的 Osgi 为 OSGI 环境下的 Osgi 对象，所有服务作用于此对象所需的基类，此处 HBundle 是热部署的
 * 新抽象模型，用于构造 OSGI 环境下的组件。
 *
 * @author lang : 2024-04-17
 */
public abstract class AbstractAmbiguity {

    private final HBundle bundle;

    protected AbstractAmbiguity(final HBundle bundle) {
        this.bundle = bundle;
    }

    public HBundle caller() {
        return this.bundle;
    }
}
