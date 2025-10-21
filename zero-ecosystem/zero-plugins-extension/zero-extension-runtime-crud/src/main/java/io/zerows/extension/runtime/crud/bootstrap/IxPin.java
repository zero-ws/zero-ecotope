package io.zerows.extension.runtime.crud.bootstrap;

import io.vertx.core.Vertx;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.epoch.database.jooq.operation.ADB;
import io.zerows.epoch.database.jooq.operation.ADJ;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.mbse.HOne;
import io.zerows.mbse.metadata.KModule;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.configuration.HRegistry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/*
 * Init Infusion for `init` static life
 */
public class IxPin implements HRegistry.Mod<Vertx> {
    /**
     * 返回单表操作的 {@link ADB} 对象，针对主模块的构造操作
     *
     * @param in {@link IxMod} 模块输入参数
     *
     * @return {@link ADB} 操作对象
     */
    public static ADB jooq(final IxMod in) {
        final Envelop envelop = in.envelop();
        return jooq(in.module(), envelop);
    }

    /**
     * 返回单表操作的 {@link ADB} 对象，针对主模块的构造操作
     *
     * @param module  {@link KModule} 模块输入参数
     * @param envelop {@link Envelop} 请求的统一资源模型
     *
     * @return {@link ADB} 操作对象
     */
    public static ADB jooq(final KModule module, final Envelop envelop) {
        final HOne<ADB> jq = HOne.jooq();
        return jq.combine(module, envelop.headers());
    }

    /**
     * 返回多表操作的 {@link ADJ} 对象，针对双模块的专用操作
     *
     * @param in      {@link IxMod} 模块输入参数
     * @param connect {@link KModule} 连接模块
     *
     * @return {@link ADJ} 操作对象
     */
    public static ADJ join(final IxMod in, final KModule connect) {
        final HOne<ADJ> jq = HOne.join();
        return jq.combine(in.module(), connect);
    }

    // ---------------------- 元数据处理 ----------------------
    public static KModule getActor(final String actor) {
        return IxDao.get(actor);
    }

    public static Set<String> getUris() {
        return IxConfiguration.getUris();
    }

    public static ConcurrentMap<String, List<WebRule>> getRules(final String actor) {
        return IxValidator.getRules(actor);
    }

    public static String getColumnKey() {
        return IxConfiguration.getField();
    }

    public static String getColumnLabel() {
        return IxConfiguration.getLabel();
    }

    // ---------------------- 注册流程 ----------------------
    /* 新版模块注册器 */
    @Override
    public Boolean configure(final Vertx vertx, final HAmbient ambient) {
        Ke.banner("「Εκδήλωση」- Crud ( Ix )");

        LOG.Init.info(IxPin.class, "IxConfiguration...");
        /* Configuration Init */
        IxConfiguration.registry(ambient);

        LOG.Init.info(IxPin.class, "IxDao...");
        /* Dao Init */
        IxDao.initWithOverwrite();

        LOG.Init.info(IxPin.class, "IxValidator...");
        /* Validator Init */
        IxValidator.init();
        return Boolean.TRUE;
    }
}
