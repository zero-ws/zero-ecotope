package io.zerows.epoch.corpus.io.management;

import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * WebSocket 路由管理器工厂，用于提取路由管理器专用，不同环境的设计如下：
 * <pre><code>
 *     非 OSGI 环境中
 *     - 1. 直接加载 SPI 提取 AxisSockFactory 的实现
 *       2. 调用 AxisSockFactory 实现中的方法提取 OAxis 路由挂载器
 *     OSGI 环境中
 *     - 1. AxisSockFactory 实现启动时直接注册到 FactorySockManager 中，由它来查找对应的实现，Service 调用
 *       2. 调用 AxisSockFactory 实现中的方法提取 OAxis 路由挂载器
 * </code></pre>
 *
 * @author lang : 2024-06-26
 */
public interface AxisSockFactory {
    /**
     * 提取路由挂载器
     *
     * @return 返回路由挂载器
     */
    OAxis getAxis();

    /**
     * 判断是否启用了 Sock 功能
     *
     * @return 如果启用了则直接返回 true
     */
    boolean isEnabled(Bundle owner);

    default OLog logger() {
        return Ut.Log.websocket(this.getClass());
    }
}
