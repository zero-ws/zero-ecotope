package io.zerows.extension.skeleton.spi;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;

/*
 * Prerequisite interface.
 * 1) Each app preparing progress should be defined by extension, not app module.
 * 2) When app has been initialized, here should be pre-condition processed first.
 * 3) This result should be JsonObject with:
 *    prerequisite = xxxx
 */
public interface ExPrerequisite {

    Cc<String, ExPrerequisite> CC_PREREQUISITE = Cc.open();

    /*
     * Initializer of method.
     */
    static ExPrerequisite of(final Class<?> clazz) {
        return CC_PREREQUISITE.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    /*
     * This workflow happened before app initialization, it means that there is no
     * application key generated in this life-cycle.
     */
    Future<JsonObject> prepare(String appName);
}
