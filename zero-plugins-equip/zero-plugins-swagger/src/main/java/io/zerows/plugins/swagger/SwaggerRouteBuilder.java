package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.swagger.v3.core.util.Yaml;

public class SwaggerRouteBuilder {

    public static void mount(Router router, OpenAPI openAPI) {
        // Static UI
        router.route("/docs/*").handler(StaticHandler.create("swagger-ui"));

        // Serve YAML
        String yaml = Yaml.pretty(openAPI);
        router.route("/openapi.yaml").handler(ctx -> {
            ctx.response()
                    .putHeader("Content-Type", "application/yaml")
                    .end(yaml);
        });
    }
}
