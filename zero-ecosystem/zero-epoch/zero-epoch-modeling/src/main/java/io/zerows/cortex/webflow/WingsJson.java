package io.zerows.cortex.webflow;

import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.app.HttpStatusCode;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class WingsJson extends WingsBase {
    @Override
    public void output(final HttpServerResponse response, final Envelop envelop) {
        if (this.isFreedom()) {
            final String content = this.toFreedom(envelop);
            if (Objects.isNull(content)) {
                /*
                 * No Content automatic
                 */
                response.setStatusCode(HttpStatusCode.NO_CONTENT.code());
                response.setStatusMessage(HttpStatusCode.NO_CONTENT.message());
                response.end(VString.EMPTY);
            } else {
                /*
                 * Freedom successful
                 */
                this.logger().info("Freedom mode enabled successfully.");
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
