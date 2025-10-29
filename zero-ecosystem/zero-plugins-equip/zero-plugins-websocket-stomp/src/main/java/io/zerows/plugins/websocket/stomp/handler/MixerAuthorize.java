package io.zerows.plugins.websocket.stomp.handler;

import io.r2mo.spi.SPI;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.cosmic.plugins.security.management.OCacheSecurity;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallProvider;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
        this.provider = SPI.findOverwrite(WallProvider.class);
    }

    @Override
    public <T> T mount(final StompServerHandler handler, final StompServerOptions option) {
        // Stomp Path Find
        final String stomp = option.getWebsocketPath();
        final AtomicReference<SecurityMeta> reference = new AtomicReference<>();

        final ConcurrentMap<String, Set<SecurityMeta>> walls = OCacheSecurity.entireWall();
        walls.forEach((path, aegisSet) -> {
            /*
             * Stomp:   /api/web-socket/stomp
             * Path:    /api/
             */
            if (!aegisSet.isEmpty() && Ut.uriMatch(stomp, path)) {
                if (LOG_FOUND.getAndSet(Boolean.FALSE)) {
                    log.info("[ ZERO ] ( Stomp ) Zero 查找到安全配置：( stomp = {}, path = {}, size = {} )",
                        stomp, path, aegisSet.size());
                }
                reference.set(aegisSet.iterator().next());
            }
        });
        final SecurityMeta config = reference.get();
        if (Objects.nonNull(config)) {
            final AuthenticationProvider provider = this.provider.provider401(this.vertx, config);
            if (LOG_PROVIDER.getAndSet(Boolean.FALSE)) {
                log.info("[ ZERO ] ( Stomp ) 安全认证器：{}", provider.getClass());
            }
            handler.authProvider(provider);
        }
        return this.finished(config);
    }
}
