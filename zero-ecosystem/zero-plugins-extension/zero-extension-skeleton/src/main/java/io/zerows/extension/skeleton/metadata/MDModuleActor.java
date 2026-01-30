package io.zerows.extension.skeleton.metadata;

import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.MDConfig;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.epoch.web.MDConnect;
import io.zerows.epoch.web.MDEntity;
import io.zerows.epoch.web.MDId;
import io.zerows.epoch.web.MDMeta;
import io.zerows.epoch.web.MDWorkflow;
import io.zerows.extension.skeleton.boot.ExAbstractHActor;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Extension 模块抽象类，启动过程中用于加载模块的专用配置，构造 {@link MDConfiguration} 对象管理，此对象管理会处理如下事情：
 * <pre>
 *     1. 由于 {@link HRegistry} 已经完成了 {@link HAmbient} 的初始化，所以此处无需处理应用环境
 *        --> 默认开始启动模块中的 {@link HActor} 时，整个应用环境已经就绪
 *     2. 扩展模块直接从此处获取模块信息，提供统一的生命周期接口来完成模块的核心启停动作
 *        --> Manager 初始化
 *        --> Configuration 配置构造：包括 Yaml 和 Json 配置
 *        --> {@link MDModuleRegistry} 构造当前 {@link HAmbient}
 *     3. 根据配置类型执行配置绑定操作，在 Manager 构造过程中完成
 *        {@link HAmbient} 自带多应用环境，内置 key = {@link HArk} 的应用映射关系
 * </pre>
 * 子类提供的支撑和选择
 * <pre>
 *     1. 子类必须实现 Manager 对象
 *        -> 内置管理所有配置
 *           {@link MDCOnfiguration}
 *           Yaml Configuration         -> 核心配置
 *           Configuration              -> 业务配置
 *     2. 子类的模块标识符 MID
 *        -> 用于标识当前模块
 *     3. 子类可选实现配置类型
 *        -> typeOfConfiguration        -> 业务配置类型
 *        -> typeOfMDC                  -> 核心配置类型
 * </pre>
 *
 * @author lang : 2025-11-04
 */
@SuppressWarnings("all")
public abstract class MDModuleActor extends ExAbstractHActor {

    private static final Cc<Class<?>, MDModuleManager> CC_MANAGER = Cc.open();

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
     * @return 加载结果（异步）
     */
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        // 1. 构造 Manager
        final MDModuleManager manager = CC_MANAGER.pick(this::manager, this.getClass());
        if (Objects.isNull(manager)) {
            this.log().info("{} \uD83D\uDFE1 `{}` 无特殊流程", KeConstant.K_PREFIX_BOOT, this.MID());
            return Future.succeededFuture(Boolean.TRUE);
        }
        this.log().info("{} \uD83D\uDFE2 `{}` ----> {}", KeConstant.K_PREFIX_BOOT, this.MID(), manager.getClass().getName());


        // 2. 绑定配置
        final Object setting = manager.setting();
        if (Objects.isNull(setting)) {
            this.configure(manager, config);
        }
        final Object configurationT = manager.config();
        if (Objects.isNull(configurationT)) {
            this.configure(manager);
        }


        // 3. 构造 HAmbient 环境特殊方法
        final MDModuleRegistry registry = MDModuleRegistry.of(this.MID());
        return registry.withAmbient(config, vertxRef).compose(ambient -> {
            Objects.requireNonNull(ambient);
            // 此处 withAmbient 有注册过程，替换原始的 Pin.configure 方法的核心逻辑
            return this.startAsync(ambient, vertxRef);
        });
    }

    // ----- 子类附加实现的方法
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        // 子类实现，若有特殊模块信息则覆盖此方法
        final Set<Future<Boolean>> arkFuture = new HashSet<>();
        ambient.app().forEach((k, v) -> arkFuture.add(this.startAsync(v, vertxRef)));
        return Fx.combineB(arkFuture);
    }

    protected Future<Boolean> startAsync(final HArk ark, final Vertx vertxRef) {
        // 子类实现，若有特殊模块信息则覆盖此方法
        return Future.succeededFuture(Boolean.TRUE);
    }

    // ----- 子类必须实现的方法
    protected abstract String MID();

    @SuppressWarnings("all")
    protected <M extends MDModuleManager> M manager() {
        return null;
    }

    // ----- 子类可选的方法
    protected <C extends MDConfig> Class<C> typeOfConfiguration() {
        return null;
    }

    protected <Y extends MDConfig> Class<Y> typeOfMDC() {
        return null;
    }

    private Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    private void configure(final MDModuleManager manager) {
        // --> 业务配置
        final MDConfiguration configuration = manager.configuration();
        if (Objects.isNull(configuration)) {
            return;
        }
        final JsonObject configurationJ = configuration.inConfiguration();
        if (Ut.isNil(configurationJ)) {
            return;
        }

        final Class<?> clsConfig = this.typeOfConfiguration();
        if (Objects.isNull(clsConfig)) {
            return;
        }

        final Object instance = Ut.deserialize(configurationJ, clsConfig);
        if (Objects.nonNull(instance)) {
            this.log().info("{} --> ⚙️ JSON / `{}` 业务配置：{}", KeConstant.K_PREFIX_BOOT, this.MID(), instance.getClass());
            manager.config(instance);
        }
    }

    private void configure(final MDModuleManager manager, final HConfig config) {
        // --> 核心配置
        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            return;
        }
        final Class<?> clsMDC = this.typeOfMDC();
        if (Objects.isNull(clsMDC)) {
            return;
        }
        // 反序列化
        final JsonObject options = config.options();
        final Object instance = Ut.deserialize(options, clsMDC);
        if (Objects.nonNull(instance)) {
            this.log().info("{} --> ⚙️ YAML / `{}` 核心配置：{}", KeConstant.K_PREFIX_BOOT, this.MID(), instance.getClass());
            manager.setting(instance);
        }
    }
}
