package io.zerows.extension.mbse.basement.util;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.shared.app.KDS;
import io.zerows.epoch.common.shared.app.KDatabase;
import io.zerows.epoch.common.shared.normalize.KIdentity;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.Database;
import io.zerows.epoch.corpus.database.cp.zdk.DS;
import io.zerows.epoch.corpus.database.cp.zdk.DataPool;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.atom.Schema;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.atom.data.DataRecord;
import io.zerows.extension.mbse.basement.osgi.spi.mixture.HLoadAtom;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;
import io.zerows.extension.mbse.basement.uca.jdbc.Pin;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.specification.modeling.operation.HLoad;

import java.util.Objects;
import java.util.function.Supplier;

class AoImpl {
    // 模型读取器
    public static final Cc<String, HLoad> CC_LOAD = Cc.openThread();
    private static final Cc<Integer, Switcher> CC_SWITCHER = Cc.open();
    private static final Cc<String, HDao> CC_T_DAO = Cc.openThread();

    /*
     * Private Method for Schema / Model
     */
    private static Schema toSchema(final String appName) {
        final Class<?> implSchema = AoStore.clazzSchema();
        return Ut.instance(implSchema, AoStore.namespace(appName));
    }

    /*
     * - Schema
     *   toSchema(String, JsonObject)
     *   toSchema(String, String)
     *
     * - Model
     *   toModel(String, JsonObject)
     *   toModel(String, String)
     *
     * - OnOff
     *   toSwitcher(KIdentity, JsonObject)
     */
    static Schema toSchema(final String appName, final JsonObject schemaJson) {
        final Schema schemaObj = toSchema(appName);
        schemaObj.fromJson(schemaJson);
        return schemaObj;
    }

    static Schema toSchema(final String appName, final String file) {
        final Schema schemaObj = toSchema(appName);
        schemaObj.fromFile(file);
        return schemaObj;
    }

    static Model toModel(final String appName) {
        final HArk ark = Ke.ark(appName);
        final Class<?> implModel = AoStore.clazzModel();
        return Ut.instance(implModel, ark);
    }

    static Switcher toSwitcher(final KIdentity identity, final JsonObject options) {
        return CC_SWITCHER.pick(() -> {
            final Class<?> implSwitcher = AoStore.clazzSwitcher();
            return Ut.instance(implSwitcher, identity, options);
        }, identity.hashCode());
        /*
        Fn.po?l(AoCache.POOL_SWITCHER, identity.hashCode(), () -> {
            final Class<?> implSwitcher = AoStore.clazzSwitcher();
            return Ut.instance(implSwitcher, identity, options);
        });*/
    }

    // ------------------- Dao / Atom -----------------

    /*
     * - DataAtom
     *   toAtom(JsonObject)
     * - AoDao
     */
    static DataAtom toAtom(final JsonObject options) {
        final String identifier = options.getString(KName.IDENTIFIER);
        final String name = options.getString(KName.NAME);
        return toAtom(name, identifier);
    }

    static DataAtom toAtom(final String appName, final String identifier) {
        final HLoad loader = CC_LOAD.pick(HLoadAtom::new);
        final HAtom atom = loader.atom(appName, identifier);
        if (atom instanceof DataAtom) {
            return (DataAtom) atom;
        } else {
            return null;
        }
    }

    static HDao toDao(final HAtom atom) {
        return Ux.channelS(DS.class, ds -> {
            /* 连接池绑定数据库 */
            final HArk ark = atom.ark();
            final DataPool pool = ds.switchDs(ark.sigma());
            if (Objects.nonNull(pool)) {
                /* 返回AoDao */
                final KDatabase database = pool.getDatabase();
                return toDao(() -> atom, database);
            } else {
                return null;
            }
        });
    }

    static HDao toDao(final Supplier<HAtom> supplier, final KDatabase database) {
        if (Objects.isNull(database)) {
            return null;
        } else {
            final HAtom atom = supplier.get();
            if (Objects.isNull(atom)) {
                return null;
            } else {
                final Pin pin = Pin.getInstance();
                return CC_T_DAO.pick(() -> pin.getDao(database).mount(atom), atom.identifier());
                // return Fn.po?lThread(AoCache.POOL_T_DAO, () -> pin.getDao(database).mount(argument), argument.identifier());
            }
        }
    }

    // ----------------- To Current ------------------
    static DataAtom toAtom(final String identifier) {
        final HArk ark = Ke.ark();
        if (Objects.nonNull(ark)) {
            final HApp app = ark.app();
            return toAtom(app.name(), identifier);
        } else {
            return null;
        }
    }

    static HRecord toRecord(final String identifier, final JsonObject data) {
        final DataAtom atom = toAtom(identifier);
        if (Objects.isNull(atom)) {
            return null;
        }
        final HRecord record = new DataRecord();
        Ut.contract(record, DataAtom.class, atom);
        return Ux.updateR(record, data);
    }

    static HDao toDao(final String identifier) {
        final HArk ark = Ke.ark();
        final DataAtom atom;
        if (Objects.nonNull(ark)) {
            final HApp app = ark.app();
            atom = toAtom(app.name(), identifier);
        } else {
            atom = null;
        }
        if (Objects.nonNull(atom)) {
            final KDS<Database> ds = ark.database();
            return toDao(() -> atom, ds.dynamic());
        } else {
            return null;
        }
    }
}
