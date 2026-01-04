package io.zerows.extension.module.rbac.plugins;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.extension.PlugAuditor;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.skeleton.common.KeIpc;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;

@Slf4j
public class PlugAuditorPin implements PlugAuditor {
    private final static String INCLUDE = "include";
    private final static String EXCLUDE = "exclude";
    private final transient JsonObject config = new JsonObject();

    @Override
    public PlugAuditor bind(final JsonObject config) {
        final JsonObject auditor;
        if (Objects.isNull(config)) {
            auditor = new JsonObject();
        } else {
            auditor = config.copy();
        }
        /*
         * Configured for empty config
         */
        final JsonArray include = auditor.getJsonArray(INCLUDE, new JsonArray());
        include.addAll(KeIpc.Audit.INCLUDE);
        auditor.put(INCLUDE, include);

        final JsonArray exclude = auditor.getJsonArray(EXCLUDE, new JsonArray());
        exclude.addAll(KeIpc.Audit.EXCLUDE);
        auditor.put(EXCLUDE, exclude);
        this.config.mergeIn(auditor);
        return this;
    }

    @Override
    public Future<Envelop> audit(final RoutingContext context,
                                 final Envelop envelop) {
        final HttpServerRequest request = context.request();
        if (this.isValid(request)) {
            final HttpMethod method = request.method();
            /* Get user id */
            final String userId = envelop.userId();
            final Instant instant = Instant.now();
            /*
             * counter is not 0, it means match
             * Find the first JsonObject instead of provide index findRunning here
             */
            if (HttpMethod.POST == method) {
                /*
                 * /api/xxx
                 * The method definition
                 * method(JsonObject data)
                 */
                envelop.value(KName.CREATED_BY, userId);
                envelop.value(KName.CREATED_AT, instant);
                envelop.value(KName.UPDATED_BY, userId);
                envelop.value(KName.UPDATED_AT, instant);
                log.info("[ XMOD ] 添加：userId = `{}`, at = `{}`", userId, instant.toString());
            } else {
                /*
                 * /api/xxx
                 * The method definition
                 * method(String, JsonObject)
                 */
                envelop.value(KName.UPDATED_BY, userId);
                envelop.value(KName.UPDATED_AT, instant);
                log.info("[ XMOD ] 更新：userId = `{}`, at = `{}`", userId, instant.toString());
            }
        } else {
            log.debug("[ XMOD ] 路径不满足: {}", request.path());
        }
        return Ux.future(envelop);
    }

    private boolean isValid(final HttpServerRequest request) {
        final JsonArray include = this.config.getJsonArray(INCLUDE);
        if (Objects.isNull(include) || include.isEmpty()) {
            /*
             * Must set `include` and `exclude`
             */
            return false;
        }
        final HttpMethod method = request.method();
        if (HttpMethod.PUT != method && HttpMethod.POST != method) {
            /*
             * Must be impact join `PUT` or `POST`
             */
            return false;
        }
        final String path = request.path();
        final long counter = include.stream().filter(Objects::nonNull)
            .map(item -> (String) item)
            .filter(path::startsWith)
            .count();
        final JsonArray exclude = this.config.getJsonArray(EXCLUDE);
        final String recovery = OCacheUri.Tool.recovery(request.path(), request.method());
        if (Objects.isNull(exclude) || exclude.isEmpty()) {
            /*
             * Exclude counter = 0, only include valid
             */
            return 0 < counter;
        } else {
            final long except = exclude.stream().filter(Objects::nonNull)
                .map(item -> (String) item)
                .filter(recovery::startsWith)
                .count();
            return 0 < counter && except <= 0;
        }
    }
}
