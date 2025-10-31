package io.zerows.plugins.excel.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.excel.metadata.ExTenant;

/**
 * @author lang : 2024-06-12
 */
public interface DataTaker {

    Cc<String, DataTaker> CC_SKELETON = Cc.openThread();

    static DataTaker ofStatic(final ExTenant tenant) {
        final String cacheKey = tenant.hashCode() + VString.SLASH + DataTakerStatic.class.getName();
        return CC_SKELETON.pick(() -> new DataTakerStatic(tenant), cacheKey);
    }

    static DataTaker ofDynamic(final ExTenant tenant) {
        final String cacheKey = tenant.hashCode() + VString.SLASH + DataTakerDynamic.class.getName();
        return CC_SKELETON.pick(() -> new DataTakerDynamic(tenant), cacheKey);
    }

    static DataTaker ofForbidden(final ExTenant tenant) {
        final String cacheKey = tenant.hashCode() + VString.SLASH + DataTakerForbidden.class.getName();
        return CC_SKELETON.pick(() -> new DataTakerForbidden(tenant), cacheKey);
    }

    Future<JsonArray> extract(JsonArray dataA, String name);
}
