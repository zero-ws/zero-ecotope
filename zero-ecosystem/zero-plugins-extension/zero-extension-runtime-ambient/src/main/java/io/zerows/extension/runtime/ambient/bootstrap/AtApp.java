package io.zerows.extension.runtime.ambient.bootstrap;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.runtime.ambient.exception._80300Exception500AmbientError;
import io.zerows.extension.runtime.ambient.exception._80301Exception500ApplicationInit;
import org.jooq.DSLContext;

import java.util.Objects;

@SuppressWarnings("all")
public class AtApp {

    /*
     * DSLContext = AppDao in
     * For multi application usage, each application should has
     * only one AppDao that in to DSLContext.
     */
    private static final Cc<String, AtApp> CC_APP = Cc.open();
    private transient final XApp app;
    private transient DSLContext context;
    private transient XAppDao dao;

    private AtApp(final String name) {
        final ADB jooq = DB.on(XAppDao.class);
        Fn.jvmKo(Objects.isNull(jooq), _80300Exception500AmbientError.class);
        /* Current */
        this.app = jooq.fetchOne(KName.NAME, name);
        Fn.jvmKo(Objects.isNull(this.app), _80301Exception500ApplicationInit.class, name);
    }

    public static AtApp create(final String name) {
        return CC_APP.pick(() -> new AtApp(name), name); // FnZero.po?l(Pool.APP_POOL, name, () -> new AtApp(name));
    }

    public XApp getApp() {
        return this.app;
    }
}
