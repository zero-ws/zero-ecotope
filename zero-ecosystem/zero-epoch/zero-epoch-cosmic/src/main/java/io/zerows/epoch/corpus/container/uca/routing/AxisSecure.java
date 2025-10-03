package io.zerows.epoch.corpus.container.uca.routing;

import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.ChainAuthHandler;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.corpus.container.handler.AuthenticateEndurer;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.epoch.corpus.web.security.store.OCacheSecurity;
import io.zerows.epoch.corpus.web.security.uca.bridge.Bolt;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 安全管理器 401 认证
 *
 * @author lang : 2024-05-04
 */
public class AxisSecure implements OAxis {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_DISABLED = new AtomicBoolean(Boolean.TRUE);
    private final Bolt bolt;

    public AxisSecure() {
        this.bolt = Bolt.get();
    }

    @Override
    public void mount(final RunServer server, final Bundle bundle) {
        /*
         * Wall mounting for authorization
         * Here create order set to remove duplicated order and re-generate the value.
         *
         * Default order: 0
         *
         * The matrix of wall
         *
         *      Wall 1           Wall 2             Wall 3
         *      0                0                  0
         *      1                1                  1
         */
        final ConcurrentMap<String, Set<Aegis>> store = OCacheSecurity.entireWall();
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
            Ut.Log.security(this.getClass()).info("Security Disabled: bolt = {}", this.bolt.getClass());
        }
    }

    private void mountAuthenticate(final RunServer server, final String path, final Set<Aegis> aegisSet) {
        final AuthenticationHandler resultHandler;
        if (VValue.ONE == aegisSet.size()) {
            // 1 = handler
            final Aegis aegis = aegisSet.iterator().next();
            resultHandler = this.bolt.authenticate(server.refVertx(), aegis);
        } else {
            // 1 < handler
            final ChainAuthHandler handler = ChainAuthHandler.all();
            aegisSet.stream()
                .map(item -> this.bolt.authenticate(server.refVertx(), item))
                .filter(Objects::nonNull)
                .forEach(handler::add);
            resultHandler = handler;
        }
        if (Objects.nonNull(resultHandler)) {
            server.refRouter().route(path).order(KWeb.ORDER.SECURE)
                .handler(resultHandler)
                .failureHandler(AuthenticateEndurer.create());
        }
    }

    private void mountAuthorization(final RunServer server, final String path, final Set<Aegis> aegisSet) {
        final AuthorizationHandler resultHandler;
        if (VValue.ONE == aegisSet.size()) {
            // 1 = handler
            final Aegis aegis = aegisSet
                .iterator().next();
            resultHandler = this.bolt.authorization(server.refVertx(), aegis);
        } else {
            // 1 = handler ( sorted )
            final Aegis aegis = new TreeSet<>(Comparator.comparingInt(Aegis::getOrder))
                .iterator().next();
            resultHandler = this.bolt.authorization(server.refVertx(), aegis);
        }
        if (IS_OUT.getAndSet(Boolean.FALSE)) {
            Ut.Log.security(this.getClass()).info("Security Selected: handler = {}, bolt = {}",
                Objects.isNull(resultHandler) ? null : resultHandler.getClass(),
                Objects.isNull(this.bolt) ? null : this.bolt.getClass());
        }
        if (Objects.nonNull(resultHandler)) {
            server.refRouter().route(path).order(KWeb.ORDER.SECURE_AUTHORIZATION)
                .handler(resultHandler)
                .failureHandler(AuthenticateEndurer.create());
        }
    }
}
