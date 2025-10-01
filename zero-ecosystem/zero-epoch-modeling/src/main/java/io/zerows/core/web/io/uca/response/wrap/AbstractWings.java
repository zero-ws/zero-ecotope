package io.zerows.core.web.io.uca.response.wrap;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.epoch.common.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.module.metadata.store.OZeroStore;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractWings implements Wings {

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

    protected Annal logger() {
        return Annal.get(this.getClass());
    }
}
