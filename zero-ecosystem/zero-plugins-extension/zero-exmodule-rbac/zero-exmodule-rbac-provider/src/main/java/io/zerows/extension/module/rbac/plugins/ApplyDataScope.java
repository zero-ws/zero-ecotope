package io.zerows.extension.module.rbac.plugins;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.webflow.UnderApply;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.skeleton.spi.ExApply;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ApplyDataScope implements UnderApply {
    private final static String EXCLUDE = "exclude";
    private final transient JsonObject config = new JsonObject();

    @Override
    public UnderApply bind(final JsonObject config) {
        final JsonObject auditor;
        if (Objects.isNull(config)) {
            auditor = new JsonObject();
        } else {
            auditor = config.copy();
        }

        final JsonArray exclude = auditor.getJsonArray(EXCLUDE, new JsonArray());
        final Set<String> excludeSet = Ut.toSet(exclude);
        final List<ExApply> applyList = HPI.findMany(ExApply.class);
        applyList.stream().map(ExApply::ruleExclude).forEach(excludeSet::addAll);
        auditor.put(EXCLUDE, Ut.toJArray(applyList));
        this.config.mergeIn(auditor);
        return this;
    }

    @Override
    public Future<Envelop> before(final RoutingContext context,
                                  final Envelop envelop) {
        final HttpServerRequest request = context.request();
        if (this.isDisabled(request)) {
            return Future.succeededFuture(envelop);
        }

        /*
         * createdBy / createdAt
         * updatedBy / updatedAt
         */
        this.applyAuditor(request, envelop);

        /*
         * appId / tenantId / sigma
         */
        this.applyScope(request, envelop);
        return Ux.future(envelop);
    }

    private void applyScope(final HttpServerRequest request, final Envelop envelop) {
        final String appIn = envelop.value(KName.APP_ID);
        if (Ut.isNil(appIn)) {
            final String appId = request.getHeader(KWeb.HEADER.X_APP_ID);
            Optional.ofNullable(appId).ifPresent(v -> envelop.value(KName.APP_ID, v));
        }

        final String tenantIn = envelop.value(KName.TENANT_ID);
        if (Ut.isNil(tenantIn)) {
            final String tenantId = request.getHeader(KWeb.HEADER.X_TENANT_ID);
            Optional.ofNullable(tenantId).ifPresent(v -> envelop.value(KName.TENANT_ID, v));
        }

        final String sigmaIn = envelop.value(KName.SIGMA);
        if (Ut.isNil(sigmaIn)) {
            final String sigma = request.getHeader(KWeb.HEADER.X_SIGMA);
            Optional.ofNullable(sigma).ifPresent(v -> envelop.value(KName.SIGMA, v));
        }

        // @Me 专用
    }

    private void applyAuditor(final HttpServerRequest request, final Envelop envelop) {
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
    }

    private boolean isDisabled(final HttpServerRequest request) {
        final HttpMethod method = request.method();
        if (HttpMethod.POST != method || HttpMethod.PUT != method) {
            // 请求方法必须是 POST 和 PUT
            return true;
        }

        final String prefix = Ut.valueString(this.config, KName.PREFIX);
        if (Ut.isNil(prefix)) {
            // 如果没有配置基础的 prefix 也直接禁用
            return true;
        }

        final JsonArray exclude = Ut.valueJArray(this.config, EXCLUDE);
        if (Ut.isNil(exclude)) {
            // 没有任何 exclude，可以不用计算，直接上
            return false;
        }

        final String path = request.path();
        final String recovery = OCacheUri.Tool.recovery(path, request.method());
        final long except = exclude.stream().filter(Objects::nonNull)
            .map(item -> (String) item)
            .filter(recovery::startsWith)
            .count();
        return 0 < except;
    }
}
