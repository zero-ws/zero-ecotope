package io.zerows.cosmic.handler;

import io.vertx.core.AbstractVerticle;
import io.zerows.cosmic.StubServer;
import io.zerows.cosmic.bootstrap.AxisCommon;
import io.zerows.cosmic.bootstrap.AxisEvent;
import io.zerows.cosmic.bootstrap.AxisExtension;
import io.zerows.cosmic.bootstrap.AxisFilter;
import io.zerows.cosmic.bootstrap.AxisMeasure;
import io.zerows.cosmic.bootstrap.AxisSecure;
import io.zerows.cosmic.bootstrap.AxisStart;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.annotations.Agent;
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
            Axis.ofOr(AxisCommon.class).mount(runServer, this.bundle);


            /*
             * 02. 安全
             *     - 401 Authentication
             *     - 403 Authorization
             */
            Axis.ofOr(AxisSecure.class).mount(runServer, this.bundle);


            /*
             * 03. 监控
             *     - Module Monitor
             *     - Service Monitor
             *     - Gateway Monitor
             */
            Axis.ofOr(AxisMeasure.class).mount(runServer, this.bundle);


            /*
             * 04. JSR-340
             *     - Filter
             *     - Listener
             */
            Axis.ofOr(AxisFilter.class).mount(runServer, this.bundle);


            /*
             * 05. 主流程
             */
            Axis.ofOr(AxisEvent.class).mount(runServer, this.bundle);


            /*
             * 06. Extension 扩展路由
             */
            Axis.ofOr(AxisExtension.class).mount(runServer, this.bundle);


            // 监听
            Axis.ofOr(AxisStart.class).mount(runServer, this.bundle);
        });
    }

    @Override
    public void stop() {
    }
}
