package io.zerows.cosmic.plugins.session;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * 这种依赖注入的模式只能走 Default 的 Vertx 来实现
 *
 * @author lang : 2025-10-14
 */
public class SessionProvider extends AddOnProvider<SessionClient> {
    public SessionProvider(final AddOn<SessionClient> addOn) {
        super(addOn);
    }
}
