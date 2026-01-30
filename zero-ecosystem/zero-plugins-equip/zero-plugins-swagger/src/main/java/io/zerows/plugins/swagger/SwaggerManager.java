package io.zerows.plugins.swagger;

import io.r2mo.typed.cc.Cc;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Vertx;

class SwaggerManager {
    private static final Cc<Integer, SwaggerConfig> CC_DOC = Cc.open();
    private static final Cc<Integer, OpenAPI> CC_API = Cc.open();

    private static final SwaggerManager INSTANCE = new SwaggerManager();

    private SwaggerManager() {
    }

    static SwaggerManager of() {
        return INSTANCE;
    }

    void registryOf(final Vertx vertxRef, final SwaggerConfig doc) {
        CC_DOC.put(vertxRef.hashCode(), doc);
        CC_API.pick(() -> SwaggerAnalyzer.compile(doc), System.identityHashCode(vertxRef));
    }

    SwaggerConfig registryOf(final Vertx vertxRef) {
        return CC_DOC.get(vertxRef.hashCode());
    }

    OpenAPI apiOf(final Vertx vertxRef) {
        return CC_API.get(vertxRef.hashCode());
    }
}
