package io.zerows.cosmic.webflow;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.specification.atomic.HPlug;

/**
 * 替换之前的 plugins 中的流程，核心流程需要加强的三个点
 * <pre>
 *     1. Audit 数据注入 / BEFORE
 *        - createdAt
 *        - createdBy
 *        - updatedAt
 *        - updatedBy
 *     2. Region 数据域 / AROUND
 *     3. Scope 范围处理 / AppId / TenantId
 * </pre>
 */
public interface UnderApply extends HPlug {

    default Future<Envelop> before(final RoutingContext context, final Envelop request) {
        return Future.succeededFuture(request);
    }

    default Future<Envelop> after(final RoutingContext context, final Envelop response) {
        return Future.succeededFuture(response);
    }
}
