package io.zerows.extension.crud.boot;

import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.epoch.metadata.KField;
import io.zerows.extension.crud.common.IxConfig;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.mbse.metadata.KColumn;
import io.zerows.mbse.metadata.KModule;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-12-24
 */
@Slf4j
class IxSetupModule extends IxSetupBase<KModule> {
    // URI 集合
    private static final Set<String> URI_SET = new HashSet<>();
    // Module 集合
    private static final MultiKeyMap<KModule> MODULE_MAP = new MultiKeyMap<>();

    private static final OCacheConfiguration STORE = OCacheConfiguration.of();

    IxSetupModule(final IxConfig config) {
        super(config);
    }

    static Set<String> stored() {
        return URI_SET;
    }

    /**
     * 此处追加入口配置，此配置中会出现重写，但重写过程中不会更新原始信息，值在原始信息基础上合并
     * <pre>
     *     特殊 MID：zero-launcher-configuration -> 最高优先级
     *     其他 {@link MDConfiguration}          -> 次优先级
     * </pre>
     */
    @Override
    public Boolean configure() {
        // 入口配置
        final MDConfiguration entryConfiguration = MDConfiguration.getInstance(IxConstant.ENTRY_CONFIGURATION);
        // 提取所有配置
        final Set<MDConfiguration> configurationSet = STORE.valueSet();
        configurationSet.forEach(configuration -> configuration.inEntity().forEach(entity -> {
            Objects.requireNonNull(entity);
            final String identifier = entity.identifier();
            // 构造 KModule
            final MDEntity entityOverwrite = entryConfiguration.inEntity(entity.identifier());
            final JsonObject moduleJ = entity.inModule();
            if (Objects.nonNull(entityOverwrite)) {
                log.info("{} `{}` <--- 被重写模块配置", KeConstant.K_PREFIX_CRUD, identifier);
                final JsonObject moduleOverwriteJ = entityOverwrite.inModule();
                moduleJ.mergeIn(moduleOverwriteJ, true);
            }
            final KModule module = Ut.deserialize(moduleJ, KModule.class);


            // 默认值处理
            this.configure(identifier, module);
            // URI 处理
            final String actor = module.getName();
            this.configure(actor, this.config().getPatterns());

            // 存储 KModule
            MODULE_MAP.put(identifier, module, module.getName());
            log.info("{} `{}` 加载完成，actor = `{}`", KeConstant.K_PREFIX_CRUD, identifier, actor);
        }));
        log.info("{} IxSetupModule 配置完成 ! Size = {}, Uris = {}", KeConstant.K_PREFIX_CRUD, MODULE_MAP.values().size(), URI_SET.size());
        return true;
    }

    /**
     * 替换原版中的 `initValues` 方法，将默认值独立出来，其中包括
     * <pre>
     *    - 针对 identifier 和 column 的默认值设置
     *    - 针对 header 的配置处理
     *    - 针对 auditor 的默认处理
     * </pre>
     * Zero 框架中，identifier 的基本绑定规则如下
     * <pre>
     *     - {@link KModule} 中的 identifier 不支持序列化，会直接使用 {@link com.fasterxml.jackson.annotation.JsonIgnore} 注解，所以它只能通过
     *       编程的方式对 identifier 进行设置
     *
     *     - 1）优先提取规则，模型标识符直接绑定文件名，此处传入的 identifier
     *       2）然后构造提取列所需的 {@link KColumn}
     *
     *     - 二者有了之后，提取 identifier 时先计算最终的 identifier，其次根据 column 计算，和原始流程不同的点在于原始流程中是以 {@link KColumn} 为
     *       主，这是早期的设计缺陷，新版直接切换到主 identifier（有很多地方都会使用）
     * </pre>
     *
     * @param module     模块定义
     * @param identifier 标识符
     *
     * @return 初始化的默认值
     */
    @SuppressWarnings("all")
    private String configure(final String identifier, final KModule module) {
        if (Ut.isNil(identifier)) {
            log.warn("{} identifier = null", KeConstant.K_PREFIX_BOOT);
            return null;
        }
        // module -> column
        this.configIdentifier(module, identifier);
        // module -> header
        this.configHeader(module);
        // module -> auditor
        this.configAuditor(module);

        return identifier;
    }

    private void configure(final String actor, final JsonArray patterns) {
        Ut.itJString(patterns)
            .map(pattern -> MessageFormat.format(pattern, actor))
            .forEach(URI_SET::add);
    }


    /**
     * 针对当前模型中的 "field" 属性给出默认值，主要针对以下配置
     * <pre><code>
     *     {
     *         "field": {
     *             "key": "key",
     *             "created": {
     *                 "by": "????",
     *                 "at": "????"
     *             },
     *             "updated": {
     *                 "by": "????",
     *                 "at": "????"
     *             }
     *         }
     *     }
     * </code></pre>
     *
     * @param module {@link KModule}
     */
    private void configAuditor(final KModule module) {
        /* Auditor Processing */
        final KField field = Objects.isNull(module.getField()) ? new KField() : module.getField();
        // key -> key
        if (Objects.isNull(field.getKey())) {
            field.setKey(KName.KEY);
        }
        // created
        final JsonObject created = Ut.valueJObject(field.getCreated());
        if (!created.containsKey(KName.AT)) {
            created.put(KName.AT, KName.CREATED_AT);
        }
        if (!created.containsKey(KName.BY)) {
            created.put(KName.BY, KName.CREATED_BY);
        }
        field.setCreated(created);
        // updated
        final JsonObject updated = Ut.valueJObject(field.getUpdated());
        if (!updated.containsKey(KName.AT)) {
            updated.put(KName.AT, KName.UPDATED_AT);
        }
        if (!updated.containsKey(KName.BY)) {
            updated.put(KName.BY, KName.UPDATED_BY);
        }
        field.setUpdated(updated);


        // module -> field
        module.setField(field);
    }

    /**
     * 根据 zero extension 的基础规范，初始化 header 部分默认值，主要针对头部定义追加需初始化的请求头信息
     * <pre><code>
     *     sigma,           X-Sigma     统一标识符
     *     language,        X-Lang      语言信息
     *     id,          X-App-Id    应用程序标识符
     *     appKey,         X-App-Key   应用程序密钥
     *     tenantId,        X-Tenant-Id 租户标识符
     * </code></pre>
     *
     * 处理的数据结构如下：
     * <pre><code>
     *     {
     *         "header": {
     *             "sigma": "X-Sigma",
     *             "language": "X-Lang",
     *             "id": "X-App-Id",
     *             "appKey": "X-App-Key",
     *             "tenantId": "X-Tenant-Id"
     *         }
     *     }
     * </code></pre>
     *
     * @param module {@link KModule}
     */
    private void configHeader(final KModule module) {
        /* Header Processing */
        final JsonObject header = Ut.valueJObject(module.getHeader());
        /* sigma -> X-Sigma */
        if (!header.containsKey(KName.SIGMA)) {
            header.put(KName.SIGMA, KWeb.HEADER.X_SIGMA);
        }
        /* language -> X-Lang */
        if (!header.containsKey(KName.LANGUAGE)) {
            header.put(KName.LANGUAGE, KWeb.HEADER.X_LANG);
        }
        /* app-id -> X-App-Id */
        if (!header.containsKey(KName.APP_ID)) {
            header.put(KName.APP_ID, KWeb.HEADER.X_APP_ID);
        }
        /* app-key -> X-App-Key */
        if (!header.containsKey(KName.APP_KEY)) {
            header.put(KName.APP_KEY, KWeb.HEADER.X_APP_KEY);
        }
        /* tenantId -> X-Tenant-Id */
        if (!header.containsKey(KName.TENANT_ID)) {
            header.put(KName.TENANT_ID, KWeb.HEADER.X_TENANT_ID);
        }


        // module -> header
        module.setHeader(header);
    }

    /**
     * 针对列的配置，解决了曾经的问题如下：
     * <pre><code>
     *     1. BUG-1：当前模型中忘记设置了 identifier 的问题
     *        原始构造中只有 Hybrid 这种模式会设置 identifier，导致静态模式下会丢失 identifier
     *        的问题，这样会导致模型的 identifier 丢失，而依赖 identifier 的所有计算会失效。
     *     2. BUG-2：{@link KColumn} 完整性问题
     *        若原始模型中配置了 {@link KColumn}，但是由于忘记配置 identifier 导致它不完整，前端
     *        读取列时会依赖它，原始流程只检查了本身为空，追加本身不为空但 identifier 为空的检查。
     * </code></pre>
     *
     * @param module     {@link KModule} 模块定义
     * @param identifier 模型标识符
     */
    private void configIdentifier(final KModule module, final String identifier) {
        // 默认先设置 identifier 到 KModule 中
        module.identifier(identifier);


        // 配置列 column
        final KColumn column;
        if (Objects.isNull(module.getColumn())) {
            column = new KColumn();
            column.setDynamic(Boolean.FALSE);
            column.setIdentifier(identifier);
        } else {
            column = module.getColumn();
            if (Ut.isNil(column.getIdentifier())) {
                column.setIdentifier(identifier);
            }
        }


        // module -> column
        module.setColumn(column);
    }

    @Override
    public ConcurrentMap<String, KModule> map() {
        final Set<String> keySet = MODULE_MAP.keySet();
        final ConcurrentMap<String, KModule> result = new ConcurrentHashMap<>();
        keySet.forEach(identifier -> result.put(identifier, MODULE_MAP.get(identifier)));
        return result;
    }

    @Override
    public KModule map(final String actor) {
        final KModule module = MODULE_MAP.getOr(actor);
        if (Objects.isNull(module)) {
            log.warn("{} Actor: identifier = `{}` 配置丢失!", IxConstant.K_PREFIX_CRUD, actor);
            return null;
        } else {
            log.info("{} Actor: identifier = `{}`", IxConstant.K_PREFIX_CRUD, actor);
            return module;
        }
    }
}
