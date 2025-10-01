package io.zerows.epoch.support;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VName;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.enums.EmApp;
import io.zerows.epoch.runtime.HMacrocosm;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.vital.HOI;

import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2023-06-06
 */
class MContext {

    static String keyApp(final String name, final String ns, final String owner) {
        return keyOwner(owner) + VString.SLASH + ns + VString.SLASH + name;
    }

    static String keyApp(final HArk ark) {
        Objects.requireNonNull(ark);
        final String ownerId = UtBase.keyOwner(Optional.ofNullable(ark.owner())
            .map(HOI::owner).orElse(VValue.DEFAULT));
        final HApp app = ark.app();
        Objects.requireNonNull(app);
        return ownerId + VString.SLASH + app.ns() + VString.SLASH + app.name();
    }

    static String keyOwner(final String id) {
        String valueOwner = UtBase.envWith(HMacrocosm.Z_TENANT, id);
        if (UtBase.isNil(valueOwner)) {
            valueOwner = VValue.DEFAULT;
        }
        return valueOwner;
    }

    static JsonObject qrApp(final HArk ark, final EmApp.Mode mode) {
        final JsonObject condition = qrCube(ark);

        final HApp app = ark.app();
        if (Objects.nonNull(app)) {
            condition.put(VName.APP_ID, app.appId());
            condition.put(VName.APP_KEY, app.option(VName.APP_KEY));
        }

        if (EmApp.Mode.CUBE != mode) {
            final HOI belong = ark.owner();
            condition.put(VName.TENANT_ID, belong.owner());
        }
        return condition;
    }

    static JsonObject qrService(final HArk ark, final EmApp.Mode mode) {
        final JsonObject condition = qrCube(ark);
        final HApp app = ark.app();
        if (Objects.nonNull(app)) {
            condition.put(VName.NAME, app.name());
            condition.put(VName.NAMESPACE, app.ns());
        }
        if (EmApp.Mode.CUBE != mode) {
            final HOI belong = ark.owner();
            condition.put(VName.TENANT_ID, belong.owner());
        }
        return condition;
    }

    private static JsonObject qrCube(final HArk ark) {
        final JsonObject condition = new JsonObject();
        condition.put(VName.SIGMA, ark.sigma());
        condition.put(VName.LANGUAGE, ark.language());
        return condition;
    }
}
