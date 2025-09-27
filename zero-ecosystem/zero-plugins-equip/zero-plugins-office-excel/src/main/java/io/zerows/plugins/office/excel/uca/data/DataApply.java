package io.zerows.plugins.office.excel.uca.data;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.plugins.office.excel.atom.ExTenant;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-06-12
 */
public class DataApply {
    private static final Cc<String, DataApply> CC_APPLY = Cc.openThread();
    private final ExTenant tenant;

    private DataApply(final ExTenant tenant) {
        this.tenant = tenant;
    }

    public static DataApply of(final ExTenant tenant) {
        return CC_APPLY.pick(() -> new DataApply(tenant), String.valueOf(tenant.hashCode()));
    }

    /*
     * Here for critical injection, mount the data of
     * {
     *      "global": {
     *      }
     * }
     * */
    public void applyData(final Set<ExTable> dataSet) {
        if (Objects.nonNull(this.tenant)) {
            final JsonObject dataGlobal = this.tenant.valueDefault();
            if (Ut.isNotNil(dataGlobal)) {
                /*
                 * New for developer account importing cross different
                 * apps
                 * {
                 *     "developer":
                 * }
                 */
                final JsonObject developer = Ut.valueJObject(dataGlobal, KName.DEVELOPER).copy();
                final JsonObject normalized = dataGlobal.copy();
                normalized.remove(KName.DEVELOPER);
                dataSet.forEach(table -> {
                    // Developer Checking
                    if ("S_USER".equals(table.getName()) && Ut.isNotNil(developer)) {
                        // JsonObject ( user = employeeId )
                        table.get().forEach(record -> {
                            // Mount Global Data
                            record.putOr(normalized);
                            // EmployeeId Replacement for `lang.yu` or other developer account
                            final String username = record.get(KName.USERNAME);
                            if (developer.containsKey(username)) {
                                record.put(KName.MODEL_KEY, developer.getString(username));
                            }
                        });
                    } else {
                        // Mount Global Data into the ingest data.
                        table.get().forEach(record -> record.putOr(normalized));
                    }
                });
            }
        }
    }
}
