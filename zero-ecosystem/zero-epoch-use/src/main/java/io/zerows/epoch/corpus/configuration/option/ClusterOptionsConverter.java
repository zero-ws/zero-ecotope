package io.zerows.epoch.corpus.configuration.option;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;

/**
 * # 「Co」 Vert.x Extension
 *
 * This class is `Converter` class of `Options`, it just like any other converters inner
 * vert.x framework. In vert.x framework, each `XOptions` contains at least one converter to
 * process `JsonObject` configuration data. It provide feature to to type checking and default
 * value injection.
 *
 * This class is ClusterOptions assist tool
 *
 * * enabled: Boolean Type
 * * manager: String class and it will be converted to `ClusterManager`
 * * options: JsonObject
 *
 * Converter for {@link ClusterOptions}
 *
 * > NOTE: This class should be generated from {@link ClusterOptions} original class
 * using Vert.x codegen, but there exist `Class<?>` type attribute, the automatic generator has
 * been ignored.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
final class ClusterOptionsConverter {

    private ClusterOptionsConverter() {
    }

    static void fromJson(final JsonObject json, final ClusterOptions obj) {
        if (json.getValue("enabled") instanceof Boolean) {
            obj.setEnabled(json.getBoolean("enabled"));
        }
        if (json.getValue(KName.OPTIONS) instanceof JsonObject) {
            obj.setOptions(json.getJsonObject(KName.OPTIONS));
        }
        final Object managerObj = json.getValue("manager");
        final Class<?> clazz = Ut.clazz(managerObj.toString());
        // If null, keep default
        final ClusterManager manager = Ut.instance(clazz);
        obj.setManager(manager);
    }
}
