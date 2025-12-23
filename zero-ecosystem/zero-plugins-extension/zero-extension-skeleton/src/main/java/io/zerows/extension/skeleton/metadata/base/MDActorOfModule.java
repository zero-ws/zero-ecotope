package io.zerows.extension.skeleton.metadata.base;

import io.r2mo.base.program.R2Vector;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.basicore.MDMeta;
import io.zerows.epoch.basicore.MDWorkflow;
import io.zerows.extension.skeleton.boot.ExAbstractHActor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.development.compiled.HBundle;

import java.net.URL;

/**
 * Extension 模块抽象类，启动过程中用于加载模块的专用配置，构造 {@link MDConfiguration} 对象管理，此对象管理会处理如下事情：
 *
 * @author lang : 2025-11-04
 */
public abstract class MDActorOfModule extends ExAbstractHActor {

    /**
     * {@link MDConfiguration} 的核心数据结构和用途
     * 基础配置：configuration.json，路径位于 plugins/{mid}/configuration.json
     * <pre>
     *   1. 变量 configurationJ
     *      基础模块配置 {@link MDConfiguration#inConfiguration()}
     *      对应 plugins/{mid}/configuration.json 文件内容
     *
     *   -------------------------------------------------------------
     *   2. 变量 entityMap
     *      实体映射结构 {@link MDConfiguration#inEntity()} 和 {@link MDConfiguration#inEntity(String)}
     *      一层结构                  二层结构                  实体
     *      Table     -> Identifier
     *      Dao Class -> Identifier
     *                              Identifier -> MDEntity
     *                                                        {@link MDEntity}
     *                                                        - {@link MDEntity#identifier()}  静态/动态建模专用标识符
     *                                                        - {@link MDEntity#inModule()}    -> zero-extension-crud     / CRUD 模块配置
     *                                                          - moduleJ
     *                                                        - {@link MDEntity#inColumns()}   -> zero-exmodule-ui        / 列配置信息
     *                                                          - columns
     *                                                        - {@link MDEntity#refConnect()}
     *                                                          - connect
     *                                                            {@link MDConnect} / 参考 MDConnect 结构
     *
     *   -------------------------------------------------------------
     *   3. 变量 connectMap
     *      实体连接映射结构 {@link MDConfiguration#inConnect()} 和 {@link MDConfiguration#inConnect(String)}
     *      一层结构                  二层结构
     *      Dao Class -> Table
     *                               Table -> {@link MDConnect} / 参考 MDConnect 结构
     *
     *   -------------------------------------------------------------
     *   4. 变量 fileSet {@link MDConfiguration#inFiles()}
     *      对应特殊插件：zero-plugins-excel 的专用导入数据目录，值如：
     *      - plugins/{mid}/modulat/???.xlsx                 模块化数据文件
     *      - plugins/{mid}/data/???.xlsx                    业务数据文件
     *      - plugins/{mid}/workflow/???.xlsx                工作流数据文件
     *      - plugins/{mid}/security/???.xlsx                安全数据文件
     *
     *   -------------------------------------------------------------
     *   5. 变量 workflowMap
     *      工作流专用配置结构 {@link MDConfiguration#inWorkflow()}
     *      一层结构
     *      definitionKey             -> {@link MDWorkflow}
     *                                   - {@link MDId}       标识专用结构
     *                                   - formSet            表单配置集合
     *                                   - formData           表单详细配置数据 / code = {@link JsonObject}
     *                                   - name               工作流名称
     *                                   - bpmn               工作流 BPMN 文件路径
     *                                   - directory          工作流目录路径
     *
     *   -------------------------------------------------------------
     *   6. 变量 pageMap
     *      前端页面专用配置 {@link MDConfiguration#inWeb()}
     *      - id                    -> {@link MDId}
     *      - configData            -> 页面配置信息 {@link JsonObject}
     *      - key                   -> 页面 code 或 标识路径
     *      - filename              -> 页面配置文件名称
     *
     *   -------------------------------------------------------------
     *   7. id 和 name
     *      - id          -> {@link MDId}
     *      - name        -> 模块名称，对应 plugins/{mid}.yaml 中配置的 name 字段
     * </pre>
     * 辅助数据结构如下：动态建模和静态建模可共享的元数据定义核心结构
     * <pre>
     *     1. {@link MDConnect}
     *     {@link MDConnect} 对应旧版 vertx-excel.yml 配置
     *     - dao                        -> 实体对应的 DAO 类
     *     - pojoFile                   -> 实体对应的 POJO 文件，对应 pojo/???.yml
     *                                     {@link R2Vector} 配置
     *     - unique                     -> 实体唯一化配置
     *     - key                        -> 实体主键字段名
     *     - {@link MDMeta}
     *         - dao                      -> 实体对应的 DAO 类（数据访问器类）
     *         - pojo                     -> 实体对应的 POJO 类（实体类）
     *         - table                    -> 实体对应的表名
     *         - isEntity                 -> true：实体类 | false：关系类
     *
     *     2. {@link MDId}
     *     - value                      -> 标识符值（一般是模块的 mid 如 zero-extension-crud）
     *     - path                       -> plugins/{value} 路径
     *     - url {@link URL}            -> 路径对应的 URL 地址，可用于读取和加载 Jar 内部路径
     *     - owner {@link HBundle}      -> Bundle 对应信息，内部可以获取 Bundle 相关资源（OSGI），其他环境也可统一
     * </pre>
     *
     * @param config   加载的配置信息
     * @param vertxRef Vertx 实例引用
     *
     * @return 加载结果（异步）
     */
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("启动扩展模块：{}", this.MID());
        final MDModuleManager manager = MDModuleManager.of(this.MID());


        // 标准配置：创建一个新的 MDConfiguration
        final MDConfiguration configuration = manager.registry(config);


        // 特殊配置：MDSetting 转换基础配置，转换必须是同步行为
        final Object setting = this.setConfig(configuration, vertxRef);
        manager.registry(this.getClass(), setting);

        return this.startAsync(configuration, vertxRef);
    }

    protected Future<Boolean> startAsync(final MDConfiguration configuration, final Vertx vertxRef) {
        // 子类实现，若有特殊模块信息则覆盖此方法
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected abstract String MID();

    /**
     * 此方法必须详细说明
     * <pre>
     *     1. 模块配置来自两个位置：
     *        vertx.yml -> 启用/禁用、核心系统配置
     *        如果这份配置没有则直接不启动当前模块，或模块运行不正常
     *        变量：{@link MDConfiguration#inSetting()}
     *     2. 业务配置：
     *        plugins/{mid}/configuration.json -> 模块的业务配置信息
     *        配置只会影响当前模块的部分业务逻辑
     *        变量：{@link MDConfiguration#inConfiguration()}
     * </pre>
     * 其中 vertx.yml 实际是绑定了 {@link HConfig} 对象的，而业务配置则是绑定的 {@link MDConfiguration}，业务配置中会包含系统配置对象，如果
     * 系统配置对象没有，则证明当前模块无需此配置，但最少应该启用 extension 基础配置。这两套配置并非所有的配置对象都有所求，所以根据实际启动过程中的
     * 资源管理来处理，微服务模式下可能部分配置并不需要。
     *
     * @param configuration 模块配置
     * @param vertxRef      Vertx实例
     *
     * @return 被转换的特殊配置对象
     */
    protected Object setConfig(final MDConfiguration configuration, final Vertx vertxRef) {
        return null;
    }

    protected static <T> T getConfig(final String mid, final Class<?> key) {
        return MDModuleManager.of(mid).getConfig(key);
    }
}
