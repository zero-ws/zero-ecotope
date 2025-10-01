package io.mature.stellar.vendor;

import io.mature.stellar.Party;
import io.mature.stellar.owner.OkA;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.datamation.KDictConfig;
import io.zerows.epoch.common.shared.datamation.KFabric;
import io.zerows.epoch.common.shared.datamation.KMap;
import io.zerows.core.database.atom.Database;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.atomic.HCopier;
import io.zerows.unity.Ux;

/**
 * 「同步」通道组装接口
 * > 该接口为同步接口，主要应用于测试环境，正式环境中则直接走表结构：`I_API/I_JOB/I_SERVICE` 配置。
 *
 * <p>
 * <title>1. 基本介绍</title>
 * 定义了通道组装专用接口，有如下用途：
 * <ul>
 * <li>- 单元测试中读取 X 推送专用的一次性配置。</li>
 * <li>- 组装 X 推送和 X 访问的基础配置。</li>
 * <li>- 提供一次性配置的字典翻译器。</li>
 * </ul>
 * 此处的 X 代表 Party B 的名称，一般是 stellar 配置的目录中的子目录名。
 * </p>
 *
 * <p>
 * <title>2. 核心结构</title>
 * 关联配置对象如下：
 * <ul>
 * <li>- 核心应用配置容器对象：{@link HArk}</li>
 * <li>- 数据库配置对象（动态配置）：{@link Database}，通常绑定到 `X_SOURCE` 表中</li>
 * <li>- 集成配置对象：{@link KIntegration}</li>
 * <li>- 字典相关信息：<br/>
 *     <ul>
 *     <li>1. 字典翻译器配置：{@link KFabric}</li>
 *     <li>2. 字段映射器配置（树型）：{@link KMap}</li>
 *     </ul>
 * </li>
 * </ul>
 * <pre><code>
 *     通常某个实现类的配置文件如下，一次配置，可用于生产环境的完整静态配置
 *     - running/external/once/database.json
 *     - running/external/once/application.json
 *     - running/external/once/integration.json
 *     实现类可以在后期使用策略或桥梁模式实现配置反射处理，您可针对 {@link PartyB} 单独初始化
 * </code></pre>
 * </p>
 *
 * <p>
 * <title>3. 初始化环境</title>
 * <pre><code>
 *     - Jooq数据库访问JDBC配置初始化
 *     - Elastic Search全文检索引擎Es配置初始化
 *     - Excel导入/导出配置初始化
 *     - Neo4j图引擎配置初始化
 *     - SharedMap专用缓存池配置初始化
 *     - Vert.x 中的 Codecs 注册——创建测试通道环境专用 Codecs
 * </code></pre>
 * </p>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface OkB extends Party, HCopier<OkB> {
    /**
     * 为 Party A 创建一个 Party B，并带上集成配置
     *
     * @param okA         {@link OkA}
     * @param integration {@link KIntegration}
     *
     * @return {@link OkB}
     */
    static OkB of(final OkA okA, final KIntegration integration) {
        return new PartyB(okA, integration);
    }

    /**
     * 集成配置对象读取方法。
     *
     * @return {@link KIntegration}
     */
    KIntegration configIntegration();

    // ---------------- 服务配置 ---------------------

    /**
     * 对应通道定义`I_SERVICE`中的`serviceConfig`属性，服务配置，构造`options`专用。
     *
     * @return {@link JsonObject}
     */
    JsonObject configService();

    /**
     * 对应的字典配置
     *
     * @return {@link KDictConfig}
     */
    KDictConfig configDict();

    /**
     * 构造映射配置对象，专用执行字段映射处理。
     *
     * @return {@link KMap}
     */
    KMap map();

    // ---------------- 字典和服务配置 ---------------------

    /**
     * 「Async」异步构造默认的字典翻译器
     * 该字典翻译器使用默认配置（identifier = ""）
     *
     * @return {@link Future}
     */
    default Future<KFabric> fabric() {
        return this.fabric(VString.EMPTY);
    }

    /**
     * 「Async」根据模型定义异步构造某一个模型的字典翻译器
     *
     * @param atom {@link DataAtom} 传入的模型定义对象`io.vertx.mod.argument.modeling.building.DataAtom`
     *
     * @return {@link Future}
     */
    default Future<KFabric> fabric(final DataAtom atom) {
        return this.fabric(atom.identifier()).compose(fabric -> {
            fabric.mapping().bind(atom.type());
            return Ux.future(fabric);
        });
    }

    /**
     * 「Async」根据统一标识符异步构造某一个模型的字典翻译器
     *
     * @param identifier {@link String} 传入的模型统一标识符
     *
     * @return {@link Future}
     */
    Future<KFabric> fabric(String identifier);
}
