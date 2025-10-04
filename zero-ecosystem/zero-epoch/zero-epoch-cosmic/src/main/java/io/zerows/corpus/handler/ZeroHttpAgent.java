package io.zerows.corpus.handler;

import io.vertx.core.AbstractVerticle;
import io.zerows.corpus.StubServer;
import io.zerows.corpus.container.AxisCommon;
import io.zerows.corpus.container.AxisEvent;
import io.zerows.corpus.container.AxisExtension;
import io.zerows.corpus.container.AxisFilter;
import io.zerows.corpus.container.AxisMeasure;
import io.zerows.corpus.container.AxisSecure;
import io.zerows.corpus.container.AxisStart;
import io.zerows.corpus.management.StoreVertx;
import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

import java.util.Set;

@Agent
public class ZeroHttpAgent extends AbstractVerticle {
    private final HBundle bundle;

    public ZeroHttpAgent() {
        this.bundle = HPI.findOverwrite(HBundle.class);
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
