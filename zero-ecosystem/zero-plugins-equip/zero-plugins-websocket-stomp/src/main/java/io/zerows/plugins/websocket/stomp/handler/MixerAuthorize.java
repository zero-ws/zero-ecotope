package io.zerows.plugins.websocket.stomp.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.cosmic.plugins.security.Bolt;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MixerAuthorize extends AbstractMixer {
    private static final AtomicBoolean LOG_FOUND = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean LOG_PROVIDER = new AtomicBoolean(Boolean.TRUE);

    private transient final Bolt bolt;

    public MixerAuthorize(final Vertx vertx) {
        super(vertx);
        this.bolt = Bolt.get();
    }

    @Override
    public <T> T mount(final StompServerHandler handler, final StompServerOptions option) {
        // Stomp Path Find
        final String stomp = option.getWebsocketPath();
        final AtomicReference<Aegis> reference = new AtomicReference<>();

        final ConcurrentMap<String, Set<Aegis>> walls = OCacheSecurity.entireWall();
        walls.forEach((path, aegisSet) -> {
            /*
             * Stomp:   /api/web-socket/stomp
             * Path:    /api/
             */
            if (!aegisSet.isEmpty() && Ut.uriMatch(stomp, path)) {
                if (LOG_FOUND.getAndSet(Boolean.FALSE)) {
                    this.logger().info(Info.SECURE_FOUND, stomp, path, String.valueOf(aegisSet.size()));
                }
                reference.set(aegisSet.iterator().next());
            }
        });
        final Aegis config = reference.get();
        if (Objects.nonNull(config)) {
            final AuthenticationProvider provider = this.bolt.authenticateProvider(this.vertx, config);
            if (LOG_PROVIDER.getAndSet(Boolean.FALSE)) {
                this.logger().info(Info.SECURE_PROVIDER, provider.getClass());
            }
            handler.authProvider(provider);
        }
        return this.finished(config);
    }
}
