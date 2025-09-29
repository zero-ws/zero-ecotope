package io.zerows.extension.runtime.ambient.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.constant.KName;
import io.zerows.core.database.jooq.operation.UxJooq;
import io.zerows.core.fn.FnZero;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.runtime.ambient.exception._500AmbientErrorException;
import io.zerows.extension.runtime.ambient.exception._500ApplicationInitException;
import io.zerows.unity.Ux;
import org.jooq.DSLContext;

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
        final UxJooq jooq = Ux.Jooq.on(XAppDao.class);
        FnZero.outWeb(null == jooq, _500AmbientErrorException.class, this.getClass());
        /* Current */
        this.app = jooq.fetchOne(KName.NAME, name);
        FnZero.outWeb(null == this.app, _500ApplicationInitException.class,
            this.getClass(), name);
    }

    public static AtApp create(final String name) {
        return CC_APP.pick(() -> new AtApp(name), name); // FnZero.po?l(Pool.APP_POOL, name, () -> new AtApp(name));
    }

    public XApp getApp() {
        return this.app;
    }
}
