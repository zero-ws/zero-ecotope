package io.zerows.plugins.swagger;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.annotations.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Worker(instances = VValue.SINGLE)
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(final Promise<Void> startPromise) {
        final ConfigRetriever retriever = ConfigRetriever.create(this.vertx);
        retriever.getConfig().onComplete(config -> {
            if (config.succeeded()) {
                final Boolean EnvRocketMq = config.result().getBoolean("Z_ENV_SWAGGER");
                if (EnvRocketMq) {
                    final Router router = Router.router(this.vertx);

                    // 挂载 Swagger 插件
                    SwaggerPlugin.mount(router, this.vertx);

                    this.vertx.createHttpServer()
                        .requestHandler(router)
                        .listen(8888)
                        .onSuccess(server -> {
                            logger.info("✅ 服务启动成功: http://localhost:8888/docs/index.html");
                            startPromise.complete();
                        })
                        .onFailure(startPromise::fail);
                } else {
                    logger.info("未启动Swagger");
                }
            }
        });
    }
}
