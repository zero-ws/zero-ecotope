package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.ChainAuthHandler;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.handler.EndurerAuthenticate;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
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
         * Wall mounting for authorization
         * Here create order set to remove duplicated order and re-generate the findRunning.
         *
         * Default order: 0
         *
         * The matrix of wall
         *
         *      Wall 1           Wall 2             Wall 3
         *      0                0                  0
         *      1                1                  1
         */
        final ConcurrentMap<String, Set<SecurityMeta>> store = OCacheSecurity.entireWall();
        store.forEach((path, securityMeta) -> {
            if (!securityMeta.isEmpty()) {
                /*
                 * The handler of 401 of each group should be
                 * 1. The path is the same
                 * 2. If the `order = 0`, re-calculate to be sure not the same ( Orders.SECURE+ )
                 * 3. Here are the set of Bolt
                 * -- Empty: return null and skip
                 * -- 1 size：return the single handler
                 * -- n size: return the handler collection of ChainAuthHandler ( all )
                 */
                // 构造 401 认证处理器
                final ChainAuthHandler handler401 = this.provider.handlerOfAuthentication(server.refVertx(), securityMeta);
                if (Objects.nonNull(handler401)) {
                    server.refRouter().route(path).order(KWeb.ORDER.SECURE)
                        .handler(handler401)
                        .failureHandler(EndurerAuthenticate.create());
                }



                /*
                 * New design for 403 access issue here to implement RBAC mode
                 * This design is optional plugin into zero system, you can enable this feature.
                 * 403 access handler must be as following
                 * 1. The uri is the same as 401, it means that the request must be passed to 401 handler first
                 * 2. The order must be after 401 Orders.SECURE
                 */
                // 构造 403 授权处理器
                final AuthorizationHandler handler403 = this.provider.handlerOfAuthorization(server.refVertx(), securityMeta);
                if (Objects.nonNull(handler403)) {
                    server.refRouter().route(path).order(KWeb.ORDER.SECURE_AUTHORIZATION)
                        .handler(handler403)
                        .failureHandler(EndurerAuthenticate.create());
                }
                if (IS_OUT.getAndSet(Boolean.FALSE)) {
                    log.info("[ ZERO ] ( Secure ) \uD83D\uDD11 安全处理器选择：authenticate = {} / authorization = {}",
                        Objects.isNull(handler401) ? null : handler401.getClass(),
                        Objects.isNull(handler403) ? null : handler403.getClass());
                }
            }
        });
        if (store.isEmpty() && IS_DISABLED.getAndSet(Boolean.FALSE)) {
            log.info("[ ZERO ] ( Secure ) ⚠️ 安全机制禁用：provider = {}", Objects.isNull(this.provider) ? null : this.provider.getClass());
        }
    }
}
