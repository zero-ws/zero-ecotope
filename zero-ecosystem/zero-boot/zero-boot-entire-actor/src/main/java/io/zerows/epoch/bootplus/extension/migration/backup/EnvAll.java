package io.zerows.epoch.bootplus.extension.migration.backup;

import io.zerows.epoch.bootplus.extension.migration.AbstractStep;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.enums.Environment;
import io.zerows.epoch.corpus.Ux;

public class EnvAll extends AbstractStep {

    private final transient MigrateStep path;

    public EnvAll(final Environment environment) {
        super(environment);
        this.path = new EnvPath(environment);
    }

    @Override
    public Future<JsonObject> procAsync(final JsonObject config) {
        this.banner("001. 初始化环境");
        return Ux.future(config)
            /* 路径处理 */
            .compose(this.path.bind(this.ark)::procAsync);
    }
}
