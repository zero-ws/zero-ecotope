package io.zerows.epoch.corpus.container.uca.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.component.log.OLog;
import io.zerows.epoch.corpus.container.uca.routing.AxisExtension;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * 扩展 OAxis 专用 Gateway，针对不同的扩展调用不同的 Factory 来实现 OAxis 的获取，此处的分流主要在于后期所有内置的 Factory 都会转换成
 * OSGI 服务，所以此处为双设计
 * <pre><code>
 *     1. 调用端
 *        {@link AxisExtension} 内置调用不同的 Gateway 做扩展，每一种 Gateway 都会对应到 Factory 工厂类中
 *        每个 Factory 都可以根据双环境（OSGI 和普通环境）来提取对应的 {@link OAxis} 路由挂载器。
 *     2. 设置端
 *        每一种特殊的 {@link OAxis} 会带有特定的 Factory 来获取，这些 Factory 在非 OSGI 环境中直接走 SPI 来获取，而 OSGI 环境中
 *        走 OSGI Service 来提取，一旦拿到引用之后就可以得到对应的 {@link OAxis} 路由挂载器了。
 * </code></pre>
 *
 * @author lang : 2024-06-27
 */
public interface OAxisGateway {

    Cc<String, OAxisGateway> CC_SKELETON = Cc.openThread();

    static OAxisGateway of(final Class<?> factoryCls) {
        return CC_SKELETON.pick(() -> Ut.instance(factoryCls), factoryCls.getName());
    }

    OAxis getAxis(Bundle owner);

    default OLog logger() {
        return Ut.Log.websocket(this.getClass());
    }
}
