package io.zerows.cortex.webflow;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.web.Envelop;
import io.zerows.management.OZeroStore;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class WingsBase implements Wings {

    protected boolean isFreedom() {
        final HSetting setting = OZeroStore.setting();
        final JsonObject launcher = setting.launcher().options();
        return launcher.getBoolean(YmlCore.FREEDOM, Boolean.FALSE);
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

    protected LogOf logger() {
        return LogOf.get(this.getClass());
    }
}
