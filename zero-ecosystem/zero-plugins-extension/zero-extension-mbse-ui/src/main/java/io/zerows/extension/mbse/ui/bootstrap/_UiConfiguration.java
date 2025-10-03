package io.zerows.extension.mbse.ui.bootstrap;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.Annal;
import io.zerows.platform.constant.VPath;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.support.Ut;
import io.zerows.extension.mbse.ui.atom.UiConfig;
import io.zerows.extension.mbse.ui.eon.UiConstant;
import io.zerows.extension.runtime.skeleton.eon.KeMsg;
import io.zerows.specification.access.app.HAmbient;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

/*
 * Configuration class initialization
 * plugin/ui/configuration.json
 *
 */
class UiConfiguration {
    private static final Annal LOGGER = Annal.get(UiConfiguration.class);
    /*
     * Static configuration mapping for column, it could not be modified
     * once configured into the column mapping
     * column map stored fileKey = filename
     * Here `fileKey` will be configured in other place to be connected.
     * The file absolute path should be `definition` + `filename` to be calculated
     * the final result.
     * Such as `ColumnStub` in CRUD, this service will be called to read
     * different columns, it support two mode:
     * Static: read column information from file ( column map support )
     * Dynamic: read column information from database ( skip file )
     */
    private static final ConcurrentMap<String, JsonArray> COLUMN_MAP =
        new ConcurrentHashMap<>();
    private static UiConfig CONFIG = null;

    static void registry(final HAmbient ambient) {
        if (null == CONFIG) {
            final MDConfiguration configuration = HExtension.getOrCreate(UiConstant.BUNDLE_SYMBOLIC_NAME);
            final JsonObject uiData = configuration.inConfiguration();
            final String module = UiConstant.BUNDLE_SYMBOLIC_NAME;
            LOG.Init.info(LOGGER, KeMsg.Configuration.DATA_J,
                module, uiData.encode());

            ambient.registry(module, uiData);

            CONFIG = Ut.deserialize(uiData, UiConfig.class);
            LOG.Init.info(LOGGER, KeMsg.Configuration.DATA_T, CONFIG.toString());
            /* Static Loading */
            initColumn(CONFIG);
            LOG.Init.info(LOGGER, "Ui Columns: Size = {0}", COLUMN_MAP.size());
        }
    }

    static UiConfig getConfig() {
        return CONFIG;
    }

    static String keyControl() {
        if (CONFIG.okCache()) {
            return CONFIG.keyControl();
        } else {
            return null;
        }
    }

    static String keyOps() {
        if (CONFIG.okCache()) {
            return CONFIG.keyOps();
        } else {
            return null;
        }
    }

    private static void initColumn(final UiConfig config) {
        /* Original `mapping` read */
        final JsonObject mapping = config.getMapping();
        final JsonObject combine = new JsonObject();
        if (Ut.isNotNil(mapping)) {
            combine.mergeIn(mapping, true);
        }
        /* Re-Calculate `mapping` configuration */
        final String configPath = config.getDefinition();
        final List<String> files = Ut.ioFiles(configPath, VString.DOT + VPath.SUFFIX.JSON);
        files.forEach(file -> {
            final String identifier = file.replace(VString.DOT + VPath.SUFFIX.JSON, VString.EMPTY);
            combine.put(identifier, file);
        });
        /* Combine File Processing */
        combine.fieldNames().forEach(fileKey -> {
            final String file = combine.getString(fileKey);
            final String filePath = configPath + '/' + file;
            /*
             * Read column from configuration path
             */
            final JsonArray columns = Ut.ioJArray(filePath);
            if (Objects.nonNull(columns) && !columns.isEmpty()) {
                COLUMN_MAP.put(fileKey, columns);
            }
        });
        /*
         * Boot: Secondary founding to pick up default configuration
         */
        final Set<HExtension> boots = HExtension.initialize();
        boots.forEach(boot -> {
            /*
             *  Crud Module
             */
            final ConcurrentMap<String, JsonArray> modules = boot.column();
            modules.forEach((moduleKey, columns) -> {
                if (!COLUMN_MAP.containsKey(moduleKey)) {
                    COLUMN_MAP.put(moduleKey, columns);
                }
            });
        });

        /* Re-Write mapping */
        CONFIG.setMapping(combine);
    }

    static JsonArray getColumn(final String key) {
        return COLUMN_MAP.getOrDefault(key, new JsonArray());
    }

    static JsonArray getOp() {
        return Ut.valueJArray(CONFIG.getOp());
    }

    static JsonArray attributes(final String key) {
        final JsonArray columns = getColumn(key);
        if (Ut.isNotNil(columns)) {
            /*
             * column transfer to
             */
            final ConcurrentMap<String, String> attributeMap = new ConcurrentHashMap<>();
            Ut.itJArray(columns).forEach(json -> {
                if (json.containsKey(KName.METADATA)) {
                    final String unparsed = json.getString(KName.METADATA);
                    final String[] parsed = unparsed.split(",");
                    if (2 < parsed.length) {
                        final String name = parsed[VValue.IDX];
                        final String alias = parsed[VValue.ONE];
                        if (!Ut.isNil(name, alias)) {
                            attributeMap.put(name, alias);
                        }
                    }
                } else {
                    final String name = json.getString("dataIndex");
                    final String alias = json.getString("title");
                    if (!Ut.isNil(name, alias)) {
                        attributeMap.put(name, alias);
                    }
                }
            });
            final JsonObject defaults = CONFIG.getAttributes();
            Ut.<String>itJObject(defaults, (alias, name) -> {
                if (!attributeMap.containsKey(name)) {
                    attributeMap.put(name, alias);
                }
            });
            /*
             * Converted to JsonArray
             */
            final JsonArray attributes = new JsonArray();
            attributeMap.forEach((name, alias) -> {
                final JsonObject attribute = new JsonObject();
                attribute.put(KName.NAME, name);
                attribute.put(KName.ALIAS, alias);
                attributes.add(attribute);
            });
            return attributes;
        } else {
            return new JsonArray();
        }
    }
}
