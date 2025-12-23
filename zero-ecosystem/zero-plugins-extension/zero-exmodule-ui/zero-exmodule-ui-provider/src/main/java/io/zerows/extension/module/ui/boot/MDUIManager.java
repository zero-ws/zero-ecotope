package io.zerows.extension.module.ui.boot;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.module.ui.common.UiConfig;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDUIManager extends MDModuleManager<Boolean, UiConfig> {
    private static MDUIManager INSTANCE;
    /**
     * 特殊结构，针对列信息进行缓存处理，存储到 Memory 中，此处的列信息会被 CRUD 调用
     * <pre>
     *     1. Static：静态列信息，主要存储在配置文件中，不会频繁变动
     *     2. Dynamic：动态列信息，可能会根据用户操作或其他条件进行变动
     * </pre>
     */
    private static final ConcurrentMap<String, JsonArray> COLUMN_MAP = new ConcurrentHashMap<>();

    private MDUIManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDUIManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDUIManager();
        }
        return INSTANCE;
    }

    // -------------------- 基础UI信息
    public String keyControl() {
        if (Objects.isNull(this.config())) {
            return null;
        }
        if (!this.config().okCache()) {
            return null;
        }
        return this.config().keyControl();
    }

    public String keyOps() {
        if (Objects.isNull(this.config())) {
            return null;
        }
        if (!this.config().okCache()) {
            return null;
        }
        return this.config().keyOps();
    }

    public JsonArray getColumn(final String identifier) {
        return COLUMN_MAP.getOrDefault(identifier, new JsonArray());
    }

    /**
     * 原始配置中的 attribute 属性配置处理
     *
     * @param identifier 标识符
     */
    public JsonArray getColumnWith(final String identifier) {
        // 提取原始列信息
        final JsonArray columns = this.getColumn(identifier);
        if (Ut.isNil(columns)) {
            return new JsonArray();
        }

        // 属性表标准化处理
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
        final JsonObject defaults = this.config().getAttributes();
        Ut.<String>itJObject(defaults, (alias, name) -> {
            if (!attributeMap.containsKey(name)) {
                attributeMap.put(name, alias);
            }
        });

        // 转换为 JsonArray 返回
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
    }

    public JsonArray getOp() {
        return this.config().getOp();
    }

    // -------------------- 初始化相关信息

    void compile(final UiConfig config) {
        Objects.requireNonNull(config);


        /* 提取原始 mapping */
        final JsonObject mapping = config.getMapping();
        final JsonObject mappingCombine = new JsonObject();
        mappingCombine.mergeIn(mapping, true);


        /* 重算 mapping 配置 */
        final String configPath = config.getDefinition();
        final List<String> files = Ut.ioFiles(configPath, VString.DOT + VValue.SUFFIX.JSON);
        files.forEach(file -> {
            final String identifier = file.replace(VString.DOT + VValue.SUFFIX.JSON, VString.EMPTY);
            mappingCombine.put(identifier, file);
        });


        /* 合并文件处理 */
        mappingCombine.fieldNames().forEach(fileKey -> {
            final String file = mappingCombine.getString(fileKey);
            final String filePath = configPath + '/' + file;
            final JsonArray columns = Ut.ioJArray(filePath);
            if (Objects.nonNull(columns) && !columns.isEmpty()) {
                COLUMN_MAP.put(fileKey, columns);
            }
        });
        config.setMapping(mappingCombine);


        /* 提取所有 MDConfiguration 中的列信息进行填充 */
        final Set<MDConfiguration> exmodules = OCacheConfiguration.of().valueSet();
        log.info("{} 系统检测 {} 个模块！！", KeConstant.K_PREFIX_BOOT, exmodules.size());
        exmodules.forEach(configuration -> {
            // 懒加载列信息
            final Set<MDEntity> entities = configuration.inEntity();
            entities.stream().filter(Objects::nonNull)
                .filter(entity -> Objects.nonNull(entity.identifier()))
                .forEach(entity -> {
                    final String identifier = entity.identifier();
                    final JsonArray columns = entity.inColumns();
                    if (!COLUMN_MAP.containsKey(identifier)) {
                        COLUMN_MAP.put(identifier, columns);
                    }
                });
        });
    }
}
