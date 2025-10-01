package io.zerows.epoch.corpus.web.websocket.router;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.atom.running.RunServer;
import io.zerows.epoch.corpus.configuration.atom.option.SockOptions;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-06-26
 */
public class AxisWs implements OAxis {
    private static final AtomicBoolean LOG_PUBLISH = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean LOG_COMPONENT = new AtomicBoolean(Boolean.TRUE);

    /**
     * WebSocket 的主入口，新版在外层 AxisExtension 部分中已经优先对 Sock 的启用与否进行过一次前置校验，在这种场景下就没有必要再考虑
     * 是否启用了 Sock 的情况，代码执行到此处证明 WebSocket 已经打开并且部署完成，此处还有一点必须注意 WebSocket 有可能会直接连接到
     * 已经运行的 HttpServer 实例中，二者依靠 port 实现共享，在这种情况下就不需要开启第二 HttpServer 服务器了。新版升级之后的改动
     * <pre><code>
     *     1. 去掉原始的 Ares 接口，将 WebSocket 的挂载统一到 OAxis 接口中
     *     2. 去掉原始的 bind 配置、bind 实例的过程，直接从参数中可提取
     *     3. 主流程中追加 OSGI 环境中的 Bundle 部分
     * </code></pre>
     *
     * @param server 运行Server实例
     * @param owner  和 OSGI 相关的 owner
     */
    @Override
    @SuppressWarnings("all")
    public void mount(final RunServer server, final Bundle owner) {
        // 提取配置
        final SockOptions sockOptions = server.configSock().options();
        if (Objects.isNull(sockOptions)) {
            return;
        }


        final HttpServerOptions serverOptions = server.config().options();


        // 是否启用了 `publish` 功能，如果启动此功能则开启广播模式
        final String publish = sockOptions.getPublish();
        if (Ut.isNotNil(publish)) {
            if (LOG_PUBLISH.getAndSet(Boolean.FALSE)) {
                this.logger().info(INFO.WS_PUBLISH, String.valueOf(serverOptions.getPort()),
                    serverOptions.getHost(), publish);
            }
            final OAxis axisPublish = OAxis.ofOr(AxisPublish.class);
            axisPublish.mount(server, owner);
        }


        // 默认组件处理，针对配置组件执行 WebSocket 相关功能，Stomp 配置
        final Class<?> axisCls = Ut.clazz(sockOptions.getComponent(), null);
        if (Objects.isNull(axisCls)) {
            // 新版组件为 null 直接跳过
            return;
        }


        if (LOG_COMPONENT.getAndSet(Boolean.FALSE)) {
            this.logger().info(INFO.WS_COMPONENT, axisCls);
        }
        final OAxis axisComponent = OAxis.ofOr((Class<OAxis>) axisCls);
        axisComponent.mount(server, owner);
    }

    private OLog logger() {
        return Ut.Log.websocket(this.getClass());
    }
}
