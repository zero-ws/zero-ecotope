package io.zerows.cosmic.bootstrap;

import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

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

    public AxisSecure() {
        // this.bolt = Bolt.get();
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
        store.forEach((path, aegisSet) -> {
            if (!aegisSet.isEmpty()) {
                /*
                 * The handler of 401 of each group should be
                 * 1. The path is the same
                 * 2. If the `order = 0`, re-calculate to be sure not the same ( Orders.SECURE+ )
                 * 3. Here are the set of Bolt
                 * -- Empty: return null and skip
                 * -- 1 size：return the single handler
                 * -- n size: return the handler collection of ChainAuthHandler ( all )
                 */
                this.mountAuthenticate(server, path, aegisSet);
                /*
                 * New design for 403 access issue here to implement RBAC mode
                 * This design is optional plugin into zero system, you can enable this feature.
                 * 403 access handler must be as following
                 * 1. The uri is the same as 401, it means that the request must be passed to 401 handler first
                 * 2. The order must be after 401 Orders.SECURE
                 */
                this.mountAuthorization(server, path, aegisSet);
            }
        });
        if (store.isEmpty() && IS_DISABLED.getAndSet(Boolean.FALSE)) {
            // log.info("[ ZERO ] ⚠️ 安全机制禁用：bolt = {}", this.bolt.getClass());
        }
    }

    private void mountAuthenticate(final RunServer server, final String path, final Set<SecurityMeta> aegisSet) {
        //        final AuthenticationHandler resultHandler;
        //        if (VValue.ONE == aegisSet.size()) {
        //            // 1 = handler
        //            final KSecurity aegis = aegisSet.iterator().next();
        //            resultHandler = this.bolt.authenticate(server.refVertx(), aegis);
        //        } else {
        //            // 1 < handler
        //            final ChainAuthHandler handler = ChainAuthHandler.all();
        //            aegisSet.stream()
        //                .map(item -> this.bolt.authenticate(server.refVertx(), item))
        //                .filter(Objects::nonNull)
        //                .forEach(handler::add);
        //            resultHandler = handler;
        //        }
        //        if (Objects.nonNull(resultHandler)) {
        //            server.refRouter().route(path).order(KWeb.ORDER.SECURE)
        //                .handler(resultHandler)
        //                .failureHandler(EndurerAuthenticate.create());
        //        }
    }

    private void mountAuthorization(final RunServer server, final String path, final Set<SecurityMeta> aegisSet) {
        //        final AuthorizationHandler resultHandler;
        //        if (VValue.ONE == aegisSet.size()) {
        //            // 1 = handler
        //            final KSecurity aegis = aegisSet
        //                .iterator().next();
        //            resultHandler = this.bolt.authorization(server.refVertx(), aegis);
        //        } else {
        //            // 1 = handler ( sorted )
        //            final KSecurity aegis = new TreeSet<>(Comparator.comparingInt(KSecurity::getOrder)).getFirst();
        //            resultHandler = this.bolt.authorization(server.refVertx(), aegis);
        //        }
        //        if (IS_OUT.getAndSet(Boolean.FALSE)) {
        //            log.info("[ ZERO ] \uD83D\uDD11 安全处理选择：handler = {}, bolt = {}",
        //                Objects.isNull(resultHandler) ? null : resultHandler.getClass(),
        //                this.bolt.getClass());
        //        }
        //        if (Objects.nonNull(resultHandler)) {
        //            server.refRouter().route(path).order(KWeb.ORDER.SECURE_AUTHORIZATION)
        //                .handler(resultHandler)
        //                .failureHandler(EndurerAuthenticate.create());
        //        }
    }
}
