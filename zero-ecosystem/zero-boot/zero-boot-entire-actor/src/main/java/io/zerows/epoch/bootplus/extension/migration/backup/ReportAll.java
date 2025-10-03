package io.zerows.epoch.bootplus.extension.migration.backup;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.AbstractStep;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.corpus.Ux;
import io.zerows.enums.Environment;

public class ReportAll extends AbstractStep {
    private transient final MigrateStep number;
    private transient final MigrateStep code;

    public ReportAll(final Environment environment) {
        super(environment);
        this.number = new ReportNumber(environment);
        this.code = new ReportCode(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("002. 报表生成");
        return Ux.future(config)
            /* Before */
            .compose(processed -> this.aspectAsync(processed, "before-report"))
            /* 生成Number */
            .compose(this.number.bind(this.ark)::procAsync)
            /* 生成Code */
            .compose(this.code.bind(this.ark)::procAsync)
            /* After */
            .compose(processed -> this.aspectAsync(processed, "after-report"));
    }
}
