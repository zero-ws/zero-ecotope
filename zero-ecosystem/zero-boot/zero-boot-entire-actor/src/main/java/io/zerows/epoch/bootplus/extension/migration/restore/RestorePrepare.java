package io.zerows.epoch.bootplus.extension.migration.restore;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.AbstractStep;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.enums.Environment;

public class RestorePrepare extends AbstractStep {
    private transient final MigrateStep cleaner;
    private transient final MigrateStep loader;
    private transient final MigrateStep finisher;

    public RestorePrepare(final Environment environment) {
        super(environment);
        this.cleaner = new MetaCleaner(environment);
        this.loader = new MetaLoader(environment);
        this.finisher = new MetaFinisher(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("002. 元数据重建");
        return Ux.future(config)
            /* 删除 DB_XXX 中现存数据 */
            .compose(this.cleaner.bind(this.ark)::procAsync)
            /* 初始化导入 DB_XXX ：OxLoader 功能 */
            .compose(this.loader.bind(this.ark)::procAsync)
            /* 初始化模型 Finisher: OxFinisher功能 */
            .compose(this.finisher.bind(this.ark)::procAsync);
    }
}
