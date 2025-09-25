package io.zerows.plugins.office.excel.uca.data;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.core.util.Ut;
import io.zerows.plugins.office.excel.atom.ExTenant;

import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-06-12
 */
class DataTakerStatic implements DataTaker {
    private final ExTenant tenant;

    DataTakerStatic(final ExTenant tenant) {
        this.tenant = tenant;
    }

    /*
     * static
     * {
     *      "dictionary"
     * }
     */
    @Override
    public Future<JsonArray> extract(final JsonArray dataA, final String name) {
        final ConcurrentMap<String, ConcurrentMap<String, String>> tree = this.tenant.tree(name);
        if (!tree.isEmpty()) {
            tree.forEach((field, map) -> Ut.itJArray(dataA).forEach(record -> {
                final String input = record.getString(field);
                if (map.containsKey(input)) {
                    final String output = map.get(input);
                    record.put(field, output);
                }
            }));
        }
        return Future.succeededFuture(dataA);
    }
}
