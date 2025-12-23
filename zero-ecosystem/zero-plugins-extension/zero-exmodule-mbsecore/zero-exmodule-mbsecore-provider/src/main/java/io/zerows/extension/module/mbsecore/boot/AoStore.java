package io.zerows.extension.module.mbsecore.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataModel;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataSchema;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.exception._11002Exception500EmptyIo;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;

import java.util.Objects;

class AoStore {
    private static final String PATH_EXCEL = "running/excel/";
    private static final String PATH_JSON = "running/json/";
    private static final String PATH_ADJUSTER = "running/adjuster/config.json";
    private static final String PATH_MODELING = "running/adjuster/modeling";
    private static final MDMBSECoreManager MANAGER = MDMBSECoreManager.of();

    static String namespace(final String appName) {
        final String prefix = MANAGER.config().getNamespace();
        if (Ut.isNil(prefix)) {
            // Default namespace
            return HApp.nsOf(appName);
        } else {
            // Configured namespace
            return Ut.fromMessage(prefix, appName);
        }
    }

    static String defineExcel() {
        final String excel = MANAGER.config().getDefineExcel();
        return Ut.isNil(excel) ? PATH_EXCEL : excel;
    }

    static String defineJson() {
        final String json = MANAGER.config().getDefineJson();
        return Ut.isNil(json) ? PATH_JSON : json;
    }

    /*
     * Two Point for Null Pointer and EmptyStream Here
     */
    static JsonObject configAdjuster() {
        String adjuster = MANAGER.config().getConfigAdjuster();
        if (Ut.isNil(adjuster)) {
            adjuster = PATH_ADJUSTER;
        }
        // Check File Existing, If not, return empty adjuster directly
        if (Ut.ioExist(adjuster)) {
            return Ut.ioJObject(adjuster);
        }
        return new JsonObject();
    }

    static JsonObject configModeling(final String filename) {
        String modeling = MANAGER.config().getConfigModeling();
        if (Ut.isNil(modeling)) {
            modeling = PATH_MODELING;
        }
        /*
         * Read critical path
         */
        final String name;
        if (modeling.endsWith("/")) {
            name = modeling + filename + VString.DOT + VValue.SUFFIX.JSON;
        } else {
            name = modeling + "/" + filename + VString.DOT + VValue.SUFFIX.JSON;
        }
        /*
         * Adjustment Processing
         */
        try {
            return Ut.ioJObject(name);
        } catch (final _11002Exception500EmptyIo ex) {
            return new JsonObject();
        }
    }

    static boolean isDebug() {
        final Boolean debug = MANAGER.config().getSqlDebug();
        if (Objects.isNull(debug)) {
            return Boolean.FALSE;
        } else {
            return debug;
        }
    }

    static Class<?> clazzPin() {
        return MANAGER.config().getImplPin();
    }

    static Class<?> clazzSchema() {
        Class<?> clazz = MANAGER.config().getImplSchema();
        if (Objects.isNull(clazz)) {
            /*
             * Default
             */
            clazz = DataSchema.class;
        }
        return clazz;
    }

    static Class<?> clazzModel() {
        Class<?> clazz = MANAGER.config().getImplModel();
        if (Objects.isNull(clazz)) {
            /*
             * Default
             */
            clazz = DataModel.class;
        }
        return clazz;
    }

    static Class<?> clazzSwitcher() {
        return MANAGER.config().getImplSwitcher();
    }
}
