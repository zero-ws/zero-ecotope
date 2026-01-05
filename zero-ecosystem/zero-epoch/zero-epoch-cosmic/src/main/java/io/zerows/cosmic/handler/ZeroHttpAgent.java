package io.zerows.cosmic.handler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.StubServer;
import io.zerows.cosmic.bootstrap.*;
import io.zerows.epoch.annotations.Agent;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

@Agent
@Slf4j
public class ZeroHttpAgent extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) {

        final HBundle bundle = HPI.findOverwrite(HBundle.class);
        /* 根据 vertx 中的 hashCode 提取当前环境运行的 RunVertx */
        final RunVertx runVertx = StoreVertx.ofOr(this.getClass()).valueGet(this.vertx.hashCode());

        /* 构造运行服务器 */
        final StubServer stubServer = StubServer.of(bundle);
        // === 看门狗保护（默认 5s 超时；失败自动打印线程转储；可重试 2 次）===
        ZeroWatchDog.watchAsyncRetry(
            this.vertx,
            () -> stubServer.createAsync(runVertx),
            ZeroHttpAgent.class.getName()
        ).onSuccess(runServer -> {
            // ============================================================
            // 🚀 核心修复：检查服务器状态
            // 如果从缓存中获取的 Server 已经处于监听状态 (actualPort > 0)，
            // 说明这是 WatchDog 重试或者是热部署导致的复用。
            // 此时绝对不能再次挂载 Handler，直接跳过所有步骤，视为成功。
            // ============================================================
            final HttpServer server = runServer.instance();
            if (server.actualPort() > 0) {
                log.info("[ ZERO ] ( Ok ) ♻️ 检测到服务器 {} 已经运行 (线程复用)，跳过步骤！{} ", server.actualPort(), server.hashCode());
                startPromise.complete();
                return;
            }
            /*
             * 01：基础路由加载
             *     - Session
             *     - Body / Content
             *     - Cors
             */
            Axis.ofOr(AxisCommon.class).mount(runServer, bundle);
            /*
             * 03. 安全
             *     - 401 Authentication
             *     - 403 Authorization
             */
            Axis.ofOr(AxisSecure.class).mount(runServer, bundle);

            /*
             * 04. 监控
             *     - Module Monitor
             *     - Service Monitor
             *     - Gateway Monitor
             */
            Axis.ofOr(AxisMeasure.class).mount(runServer, bundle);

            /*
             * 05. JSR-340
             *     - Filter
             *     - Listener
             */
            Axis.ofOr(AxisFilter.class).mount(runServer, bundle);

            /*
             * 06. 主流程
             */
            Axis.ofOr(AxisEvent.class).mount(runServer, bundle);

            /*
             * 07. Extension 扩展路由
             */
            Axis.ofOr(AxisExtension.class).mount(runServer, bundle);

            /*
             * 08. Swagger 挂载
             */
            Axis.ofOr(AxisSwagger.class).mount(runServer, bundle);

            /*
             * 09. 启动完成监听
             */
            Axis.ofOr(AxisStart.class).mount(runServer, bundle);

            startPromise.complete();

        }).onFailure(error -> {
            log.error("[ ZERO ] RunServer 初始化失败：{}", error.toString());
            log.debug("[ ZERO ] 失败堆栈：", error);
            startPromise.fail(error);
        });
    }
}
