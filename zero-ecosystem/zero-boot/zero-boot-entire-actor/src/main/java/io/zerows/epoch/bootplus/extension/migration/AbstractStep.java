package io.zerows.epoch.bootplus.extension.migration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.bootplus.extension.refine.Ox;
import io.zerows.metadata.app.KDS;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.Database;
import io.zerows.enums.Environment;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.extension.runtime.ambient.agent.service.DatumService;
import io.zerows.extension.runtime.ambient.agent.service.DatumStub;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.operation.HDao;

public abstract class AbstractStep implements MigrateStep {
    /*
     * 子类共享（Development/Production）
     */
    protected final transient Environment environment;
    /*
     * 子类共享：XNumber, XTabular
     */
    protected final transient DatumStub stub = Ut.singleton(DatumService.class);
    /*
     * 子类共享：XApp, XSource
     */
    protected transient HArk ark;

    public AbstractStep(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public MigrateStep bind(final HArk app) {
        /*
         * 数据库切换隔离操作
         */
        // this.app = DatabaseSwitcher.on(app);
        this.ark = app;
        return this;
    }

    protected HDao ioDao(final String identifier) {
        final HApp app = this.ark.app();
        final DataAtom atom = Ox.toAtom(app.option(KName.APP_ID), identifier);
        final KDS<Database> ds = this.ark.database();
        return Ao.toDao(atom, ds.dynamic());
    }

    protected String ioRoot(final JsonObject config) {
        final String output = config.getString("output");
        String folder = Ox.toRoot("migration/" + output, this.environment);
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        return folder;
    }

    protected Future<JsonArray> writeAsync(final JsonArray combined, final String file) {
        Ox.LOG.Shell.info(this.getClass(), "写入数据（A)：{0}", file);
        /*
         * 过滤 null
         */
        final JsonArray normalized = new JsonArray();
        Ut.itJArray(combined).forEach(normalized::add);
        Ut.ioOut(file, normalized);
        return Ux.future(normalized);
    }

    protected Future<JsonArray> writeCompressAsync(final JsonArray combined, final String file) {
        Ox.LOG.Shell.info(this.getClass(), "写入压缩数据（A)：{0}", file);
        final JsonArray normalized = new JsonArray();
        Ut.itJArray(combined).forEach(normalized::add);
        Ut.ioOutCompress(file, normalized);
        return Ux.future(normalized);
    }

    protected void banner(final String title) {
        System.out.println();
        System.out.println("========> " + title + "         ");
        System.out.println();
    }

    /*
     * 配置层专用处理，注意合约模式
     */
    public Future<JsonObject> aspectAsync(final JsonObject config, final String key) {
        return Around.create(this.environment).bind(this.ark)
            .aspectAsync(config, key);
    }
}
