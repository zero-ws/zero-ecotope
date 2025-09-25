package io.zerows.core.web.invocation.micro.uddi;

import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.store.OZeroStore;

class UddiConfig {

    static Class<?> registry() {
        final JsonObject data = uddiJson();
        final String component = data.getString("registry");
        return Ut.clazz(component, null);
    }

    static Class<?> jet() {
        final JsonObject data = uddiJson();
        final String component = data.getString("discovery");
        return Ut.clazz(component, null);
    }

    static Class<?> client() {
        final JsonObject data = uddiJson();
        final String component = data.getString("client");
        return Ut.clazz(component, null);
    }

    private static JsonObject uddiJson() {
        /*
         * configuration in vertx-tp.yml
         */
        final JsonObject data = OZeroStore.option("uddi");
        return Ut.valueJObject(data);
    }
}
