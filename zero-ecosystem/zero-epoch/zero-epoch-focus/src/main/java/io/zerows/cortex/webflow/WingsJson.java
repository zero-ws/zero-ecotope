package io.zerows.cortex.webflow;

import io.r2mo.spi.SPI;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class WingsJson extends WingsBase {
    WingsJson(final Vertx vertxRef) {
        super(vertxRef);
    }

    @Override
    public void output(final HttpServerResponse response, final Envelop envelop) {
        if (this.isFreedom()) {
            final String content = this.toFreedom(envelop);
            if (Objects.isNull(content)) {
                /*
                 * No Content automatic
                 */
                final WebState state = SPI.V_STATUS.ok204();
                response.setStatusCode(state.state());
                response.setStatusMessage(state.name());
                response.end(VString.EMPTY);
                response.end(VString.EMPTY);
            } else {
                /*
                 * Freedom successful
                 */
                log.info("[ ZERO ] Freedom 模式已启用 successfully.");
                response.end(content);
            }
        } else {
            /*
             * Default String mode
             * 1. Content-Type is `* / *` formatFail
             * 2. Replied body directly
             */
            response.end(envelop.outString());
        }
    }
}
