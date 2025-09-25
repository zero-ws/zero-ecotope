package io.zerows.plugins.office.excel.uca.data;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.core.util.Ut;
import io.zerows.plugins.office.excel.atom.ExTenant;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-06-12
 */
class DataTakerForbidden implements DataTaker {
    private final ExTenant tenant;

    DataTakerForbidden(final ExTenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public Future<JsonArray> extract(final JsonArray dataA, final String name) {
        final ConcurrentMap<String, Set<String>> forbidden = this.tenant.valueCriteria(name);
        if (forbidden.isEmpty()) {
            return Future.succeededFuture(dataA);
        } else {
            final JsonArray normalized = new JsonArray();
            Ut.itJArray(dataA).filter(item -> forbidden.keySet().stream().allMatch(field -> {
                if (item.containsKey(field)) {
                    final Set<String> values = forbidden.get(field);
                    final String value = item.getString(field);
                    return !values.contains(value);
                } else {
                    return true;
                }
            })).forEach(normalized::add);
            return Future.succeededFuture(normalized);
        }
    }
}
