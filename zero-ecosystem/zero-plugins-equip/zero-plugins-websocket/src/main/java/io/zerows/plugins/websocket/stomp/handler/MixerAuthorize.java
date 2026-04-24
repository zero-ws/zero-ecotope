package io.zerows.plugins.websocket.stomp.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallProvider;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class MixerAuthorize extends AbstractMixer {
    private static final AtomicBoolean LOG_FOUND = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean LOG_PROVIDER = new AtomicBoolean(Boolean.TRUE);

    private final WallProvider provider;

    public MixerAuthorize(final Vertx vertx) {
        super(vertx);
        this.provider = HPI.findOverwrite(WallProvider.class);
    }

    @Override
    public <T> T mount(final StompServerHandler handler, final StompServerOptions option) {
        // Stomp Path Find
        final String stomp = option.getWebsocketPath();
        final Set<SecurityMeta> matched = new TreeSet<>();

        final ConcurrentMap<String, Set<SecurityMeta>> walls = OCacheSecurity.entireWall();
        walls.forEach((path, aegisSet) -> {
            /*
             * Stomp:   /api/web-socket/stomp
             * Path:    /api/
             */
            if (!aegisSet.isEmpty() && Ut.uriMatch(stomp, path)) {
                if (LOG_FOUND.getAndSet(Boolean.FALSE)) {
                    log.info("[ PLUG ] ( Stomp ) Zero found security config: ( stomp = {}, path = {}, size = {} )",
                        stomp, path, aegisSet.size());
                }
                matched.addAll(aegisSet);
            }
        });
        final Set<SecurityMeta> configSet = matched.isEmpty() ? null : Set.copyOf(matched);
        if (Objects.nonNull(configSet)) {
            final AuthenticationProvider provider = this.provider.providerOfAuthentication(this.vertx, configSet);
            if (LOG_PROVIDER.getAndSet(Boolean.FALSE)) {
                log.info("[ PLUG ] ( Stomp ) security authentication provider: {}", provider.getClass());
            }
            handler.authProvider(provider);
        }
        final SecurityMeta config = matched.isEmpty() ? null : matched.iterator().next();
        return this.finished(config);
    }
}
