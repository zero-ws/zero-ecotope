package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.handler.EndurerAuthenticate;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallHandler;
import io.zerows.sdk.security.WallProvider;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 安全管理器 401 认证
 *
 * @author lang : 2024-05-04
 */
@Slf4j
public class AxisSecure implements Axis {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_DISABLED = new AtomicBoolean(Boolean.TRUE);
    private final WallProvider provider;

    public AxisSecure() {
        this.provider = HPI.findOverwrite(WallProvider.class);
    }

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        /*
         * 认证专用安全拦截器，Wall 带有 @Wall 注解的元数据信息，语义上是安全墙
         * 此处执行流程：
         * - 使用 Order 对 Handler 进行编排
         * - 可支持多个 @Wall 注解在同一路径上并且共存
         * - 支持 401 认证和 403 授权两种处理器
         *
         * Matrix 设计思想
         *       Wall 1           Wall 2             Wall 3
         *       0                0                  0
         *       1                0                  1
         *       0                1                  1
         */
        final ConcurrentMap<String, Set<SecurityMeta>> store = OCacheSecurity.entireWall();
        store.forEach((path, securityMeta) -> {
            if (!securityMeta.isEmpty()) {
                /*
                 * 每一组 401 Provider：
                 * - 每一组的 Provider 过滤的 path 是相同的
                 * - 若 order = 0，则重新计算顺序，确保不冲突（ Orders.SECURE+ ）
                 * 此处的执行集合会有三种：
                 * - 空集合：返回 null，跳过
                 * - 单一集合：返回单一 Handler
                 * - 多元素集合：返回 ChainAuthHandler（全部）
                 */
                // 构造 401 认证处理器
                final WallHandler handler401 = this.provider.handlerOfAuthentication(server.refVertx(), securityMeta);
                if (Objects.nonNull(handler401)) {
                    server.refRouter().route(path).order(KWeb.ORDER.SECURE)
                        .handler(handler401)
                        .failureHandler(EndurerAuthenticate.create());
                }



                /*
                 * 403 授权处理器设计说明
                 * 1. 403 授权处理器必须在 401 认证处理器之后执行
                 * 2. 403 授权处理器的路径必须和 401 认证处理器一致
                 * 3. 403 授权处理器可以是单一处理器，也可以是链式处理器
                 * 4. 403 授权处理器的执行顺序必须在 Orders.SECURE 之后
                 */
                // 构造 403 授权处理器
                final AuthorizationHandler handler403 = this.provider.handlerOfAuthorization(server.refVertx(), securityMeta);
                if (Objects.nonNull(handler403)) {
                    server.refRouter().route(path).order(KWeb.ORDER.SECURE_AUTHORIZATION)
                        .handler(handler403)
                        .failureHandler(EndurerAuthenticate.create());
                }
                if (IS_OUT.getAndSet(Boolean.FALSE)) {
                    log.info("[ ZERO ] ( Secure ) \uD83D\uDD11 安全处理器 {} / 选择：authenticate = {},authorization = {}",
                        path,
                        Objects.isNull(handler401) ? null : handler401.getClass(),
                        Objects.isNull(handler403) ? null : handler403.getClass()
                    );
                }
            }
        });
        if (store.isEmpty() && IS_DISABLED.getAndSet(Boolean.FALSE)) {
            log.debug("[ ZERO ] ( Secure ) ⚠️ 安全机制禁用：provider = {}", Objects.isNull(this.provider) ? null : this.provider.getClass());
        }
    }
}
