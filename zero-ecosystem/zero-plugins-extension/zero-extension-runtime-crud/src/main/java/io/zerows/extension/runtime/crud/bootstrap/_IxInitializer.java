package io.zerows.extension.runtime.crud.bootstrap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.mbse.metadata.KColumn;
import io.zerows.mbse.metadata.KModule;
import io.zerows.epoch.metadata.KField;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2023-08-18
 */
class IxInitializer {
    /**
     * 替换原始版本中 IxDao 的 `initValues` 方法，将默认值独立出来，其中包括
     * <pre><code>
     *     - 针对 identifier 和 column 的默认值设置
     *     - 针对 header 的配置处理
     *     - 针对 auditor 的默认值处理
     * </code></pre>
     *
     * 在整个 Zero 环境中，identifier 的基本绑定规则如下
     * <pre><code>
     *     - {@link KModule} 中的 identifier 不支持序列化，
     *       直接使用 {@link com.fasterxml.jackson.annotation.JsonIgnore} 注解
     *       所以只能通过编程的方式对 identifier 进行设置
     *
     *     - 1）优先提取规则，模型标识符直接绑定文件名，此处传入的 identifier
     *       2）然后构造提取列所需的 {@link KColumn}
     *
     *     - 二者有了之后，提取 identifier 的时优先提取 identifier，其次根据 column 计算
     *       和原始流程不同的点在于，原始流程中是以 {@link KColumn}
     *       为主，这是当时设计上的缺陷，新版切换到主 identifier（有很多地方都会使用）
     * </code></pre>
     *
     * @param module     {@link KModule} 模块定义
     * @param identifier 模型标识符
     *
     * @return {@link String} 初始化的默认值
     */
    static String configure(final KModule module, final String identifier) {
        Objects.requireNonNull(module);
        if (Ut.isNil(identifier)) {
            // 截断返回，表示当前模型任何信息都没有提取到
            Ix.LOG.Init.warn(IxInitializer.class, "identifier = null");
            return null;
        }

        // module -> column
        configIdentifier(module, identifier);
        // module -> header
        configHeader(module);
        // module -> auditor
        configAuditor(module);

        return identifier;
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
    private static void configAuditor(final KModule module) {
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
    private static void configHeader(final KModule module) {
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
    private static void configIdentifier(final KModule module, final String identifier) {
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
}
