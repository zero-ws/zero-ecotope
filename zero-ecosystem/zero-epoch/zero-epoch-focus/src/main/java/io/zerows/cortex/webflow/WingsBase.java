package io.zerows.cortex.webflow;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.spec.YmSpec;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class WingsBase implements Wings {

    private final Vertx vertxRef;
    private HConfig mvcConfig;

    WingsBase(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    protected HConfig mvcConfig() {
        if (Objects.isNull(this.mvcConfig)) {
            this.mvcConfig = NodeStore.findInfix(this.vertxRef, EmApp.Native.MVC);
        }
        return this.mvcConfig;
    }

    protected boolean isFreedom() {
        final JsonObject options = this.mvcConfig().options();
        if (Ut.isNil(options)) {
            return false;
        }
        return options.getBoolean(YmSpec.vertx.mvc.freedom, Boolean.FALSE);
    }

    protected String toFreedom(final Envelop envelop) {
        final JsonObject input = envelop.outJson();
        if (Ut.isNil(input)) {
            return null;
        } else {
            if (input.containsKey("data")) {
                final Object value = input.getValue("data");
                return Objects.isNull(value) ? null : value.toString();
            } else {
                return input.encode();
            }
        }
    }
}
