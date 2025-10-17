package io.zerows.extension.mbse.action.osgi.spi;

import io.r2mo.function.Fn;
import io.vertx.core.MultiMap;
import io.zerows.epoch.database.Database;
import io.zerows.epoch.database.cp.DS;
import io.zerows.epoch.database.cp.DataPool;
import io.zerows.extension.mbse.action.exception._80412Exception501DataSource;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.platform.metadata.OldKDS;
import io.zerows.specification.app.HArk;

import java.util.Objects;

/*
 * Dynamic Data Source
 */
public class DynamicDs implements DS {

    @Override
    public DataPool switchDs(final MultiMap headers) {
        final HArk ark = Ke.ark(headers);
        Fn.jvmKo(Objects.isNull(ark), _80412Exception501DataSource.class, headers.toString());
        return this.getDs(ark);
    }

    @Override
    public DataPool switchDs(final String sigma) {
        final HArk ark = Ke.ark(sigma);
        Fn.jvmKo(Objects.isNull(ark), _80412Exception501DataSource.class, sigma);
        return this.getDs(ark);
    }

    private DataPool getDs(final HArk ark) {
        /*
         * DataPool get here，For each database, it's two
         * 1) Default database with or without auto commit;
         * 2) Remove auto commit to switch to auto commit = true, a new database
         * 3) Auto commit database will be managed by DataPool, it could switch by DataPool itself
         */
        final OldKDS<Database> ds = ark.database();
        final Database database = ds.dynamic();
        return DataPool.createAuto(database);
    }
}
