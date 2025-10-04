package io.zerows.extension.commerce.rbac.uca.timer;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-09-16
 */
class ScClockImage extends AbstractClock<String> {
    private static final ScConfig CONFIG = ScPin.getConfig();

    ScClockImage(final HBundle bundle) {
        super(bundle, ScConstant.POOL_CODE_IMAGE);
    }

    @Override
    public String generate(final JsonObject config) {
        final int length = CONFIG.getImageLength();
        final String imageCode = Ut.randomCaptcha(length);
        this.logger().info("Generated Image Code: {}", imageCode);
        return imageCode;
    }

    @Override
    public <R> Future<R> verify(final String stored, final String waiting, final String identity) {
        return null;
    }

    @Override
    public int configTtl() {
        return CONFIG.getImageExpired();
    }
}
