package io.zerows.epoch.bootplus.extension.migration.restore;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.AbstractStep;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.enums.Environment;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.extension.runtime.skeleton.boot.supply.DataImport;

import static io.zerows.epoch.bootplus.extension.refine.Ox.LOG;

public class MetaFinisher extends AbstractStep {
    private final transient MigrateStep report;
    private final transient MigrateStep limit;

    public MetaFinisher(final Environment environment) {
        super(environment);
        this.report = new MetaReport(environment);
        this.limit = new MetaLimit(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("002.3. 重新建模");
        /* XApp */
        final DataImport importer = DataImport.of();
        return importer.loadAsync(Ao.PATH.PATH_EXCEL + "schema/").compose(nil -> {
            LOG.Shell.info(this.getClass(), "建模数据已经成功导入到系统！Successfully");
            return Ux.future(config)
                /* Meta 专用报表 */
                .compose(this.report.bind(this.ark)::procAsync)
                /* Meta 建模修正数据 */
                .compose(this.limit.bind(this.ark)::procAsync);
        });
    }
}
