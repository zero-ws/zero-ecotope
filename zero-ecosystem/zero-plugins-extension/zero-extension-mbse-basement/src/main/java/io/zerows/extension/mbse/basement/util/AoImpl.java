package io.zerows.extension.mbse.basement.util;

import io.r2mo.base.dbe.Database;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.atom.Schema;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.osgi.spi.mixture.HLoadAtom;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;
import io.zerows.extension.mbse.basement.uca.jdbc.Pin;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.platform.metadata.KIdentity;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.specification.modeling.operation.HLoad;
import io.zerows.support.Ut;

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
        /* 数据连接池从应用中绑定 */
        final HArk ark = atom.ark();
        final Database database = ark.database();
        Objects.requireNonNull(database, "[ PLUG ] 数据库配置不可为空，请检查应用配置！");
        return toDao(() -> atom, database);
    }

    static HDao toDao(final Supplier<HAtom> supplier, final Database database) {
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
        final HRecord record = AoData.record(atom);
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
            final Database database = ark.database();
            return toDao(() -> atom, database);
        } else {
            return null;
        }
    }
}
