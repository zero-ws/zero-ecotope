package io.zerows.epoch.corpus.container.verticle;

import io.vertx.core.AbstractVerticle;
import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.corpus.container.store.under.StoreVertx;
import io.zerows.epoch.corpus.container.uca.routing.*;
import io.zerows.epoch.corpus.container.uca.server.StubServer;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.atom.running.RunServer;
import io.zerows.epoch.corpus.model.atom.running.RunVertx;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.util.Set;

@Agent
public class ZeroHttpAgent extends AbstractVerticle {
    private final Bundle bundle;

    public ZeroHttpAgent() {
        this.bundle = FrameworkUtil.getBundle(this.getClass());
    }

    @Override
    public void start() {
        /* 提取当前环境运行的 RunVertx 实例，针对此实例执行相关操作 */
        final RunVertx runVertx = StoreVertx.ofOr(this.getClass()).valueGet(this.vertx.hashCode());

        /* 构造运行的服务器 */
        final StubServer stubServer = StubServer.of(this.bundle);
        final Set<RunServer> serverWait = stubServer.createAsync(runVertx);
        serverWait.forEach(runServer -> {
            /*
             * 01: 基础路由加载
             *     - Session
             *     - Body / Content
             *     - Cors
             */
            OAxis.ofOr(AxisCommon.class).mount(runServer, this.bundle);


            /*
             * 02. 安全
             *     - 401 Authentication
             *     - 403 Authorization
             */
            OAxis.ofOr(AxisSecure.class).mount(runServer, this.bundle);


            /*
             * 03. 监控
             *     - Module Monitor
             *     - Service Monitor
             *     - Gateway Monitor
             */
            OAxis.ofOr(AxisMeasure.class).mount(runServer, this.bundle);


            /*
             * 04. JSR-340
             *     - Filter
             *     - Listener
             */
            OAxis.ofOr(AxisFilter.class).mount(runServer, this.bundle);


            /*
             * 05. 主流程
             */
            OAxis.ofOr(AxisEvent.class).mount(runServer, this.bundle);


            /*
             * 06. Extension 扩展路由
             */
            OAxis.ofOr(AxisExtension.class).mount(runServer, this.bundle);


            // 监听
            OAxis.ofOr(AxisStart.class).mount(runServer, this.bundle);
        });
    }

    @Override
    public void stop() {
    }
}
