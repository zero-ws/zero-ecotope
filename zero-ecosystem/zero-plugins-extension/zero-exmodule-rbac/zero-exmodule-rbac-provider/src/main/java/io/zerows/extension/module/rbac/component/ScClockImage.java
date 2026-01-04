package io.zerows.extension.module.rbac.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.boot.MDRBACManager;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2024-09-16
 */
@Slf4j
class ScClockImage extends ScClockBase<String> {
    private static final ScConfig CONFIG = MDRBACManager.of().config();

    ScClockImage(final HBundle bundle) {
        super(bundle, ScConstant.POOL_CODE_IMAGE);
    }

    @Override
    public String generate(final JsonObject config) {
        final int length = CONFIG.getImageLength();
        final String imageCode = Ut.randomCaptcha(length);
        log.info("[ XMOD ] ( RBAC ) 生成图片验证码：{}", imageCode);
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
