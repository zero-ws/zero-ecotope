package io.zerows.core.web.mbse.atom.specification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.core.database.jooq.JooqPin;
import io.zerows.core.util.Ut;
import io.zerows.epoch.common.uca.aop.Aspect;
import io.zerows.epoch.enums.EmDS;
import io.zerows.epoch.integrated.jackson.databind.ClassDeserializer;
import io.zerows.epoch.integrated.jackson.databind.ClassSerializer;
import io.zerows.epoch.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.epoch.integrated.jackson.databind.JsonObjectSerializer;
import io.zerows.module.domain.atom.specification.KField;
import io.zerows.module.domain.atom.specification.KJoin;
import io.zerows.module.domain.atom.specification.KPoint;
import io.zerows.module.domain.atom.specification.KTransform;
import io.zerows.module.domain.uca.destine.Hymn;

import java.io.Serializable;
import java.util.Objects;

/**
 * 模型定义专用配置，所有模型定义主要位于目录中
 * <pre><code>
 *     1. 启动器
 *        src/main/resources/plugin/crud/xxxx.json
 *        src/main/resources/plugin/ui/xxxx.json
 *     2. 模块化
 *        src/main/resources/plugin/{module}/oob/module/xxx.json
 *        其中 {module} 是模块名称
 * </code></pre>
 * 数据结构如下
 * <pre><code>
 * {
 *     "name": "模型名称，此名称会作为 /api/:actor 中的 actor 参数",
 *     "pojo": "（一般转换成遗留系统）当模型启用 Pojo 映射时专用",
 *     "mode": "数据库存储位置，{@link EmDS.Stored} 枚举值，有五种",
 *     "modeKey": "当 mode = EXTENSION 时，此值必须，用于存储在 Extension 中的 key 值",
 *     "field": "{@link KField}",
 *     "column": "{@link KColumn}"
 * }
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KModule implements Serializable {
    /**
     * 模块名称，此名称并非模型的 identifier，而是 /api/:actor 中的 actor 参数，最终会转换成 API 定义中的路径，需要注意的是此路径在整个容器环境中都是唯一的，且您的项目一旦启用了 zero-crud 之后，此名称必须是全容器中唯一的，而且开发人员必须特别熟悉 CRUD 中提供的十五个标准化接口，否则会导致 RESTful 接口规范的冲突。
     */
    private String name;


    /**
     * 针对部分遗留系统，zero 中支持在 /pojo 目录之下存储一层属性映射，映射会存储在 yml 文件中，描述了旧属性和新属性之间的映射关系，这种映射关系会在数据格式转换过程中使用，且 Zero 框架本身带了这层映射的逻辑，直接配置就可以了。即您不需要做任何开发部分的工作就可以实现模型的属性映射
     */
    private String pojo;


    /**
     * 和数据源相关的模式选择，通常会有五种核心模式，位于 {@link EmDS.Stored} 枚举变量中，现阶段支持的值如：
     * <pre><code>
     *     - PRIMARY：主数据库，配置在 vertx-jooq.yml 中的数据库
     *     - HISTORY：历史数据库，启用了 Trash 功能之后的数据库
     *     - WORKFLOW：工作流数据库，启用了工作流功能之后的数据库
     *     - DYNAMIC：动态数据库，存储于 X_SOURCE 中的数据库
     *     - EXTENSION：扩展数据库，可使用插件挂载额外的数据源实现模型定义
     * </code></pre>
     */
    private String mode;


    /**
     * 跟随 mode = EXTENSION 的专用配置，当您需要自定义数据源时，自定义数据源的 modeKey 会让您的模型仓库中支持模型查找相关功能，如此一来您就可以直接在模型仓库中根据 modeKey 查找对应的模型定义，而不需要在代码中写死。从模型库中搜索模型信息具备唯一检索的主动权，这是 Zero 框架的核心功能之一。
     */
    private String modeKey;     // mode = EXTENSION


    /**
     * 当前主模型的特殊属性描述，详情参考 {@link KField}
     * <pre><code>
     *      - 主键
     *      - 序号属性
     *      - 业务标识规则
     *      - Auditor属性
     *      - 复杂数据结构 {@link JsonObject} / {@link io.vertx.core.json.JsonArray}
     *      - 附件属性
     * </code></pre>
     */
    private KField field;


    /**
     * 和模型相关的列连接信息，List列表中列连接分三大类，其中有两类是在后端 {@link KColumn} 处理。
     * <pre><code>
     *     1. 前端 UI.json 中直接定义
     *     2. 后端定义静态列，不访问 UI_COLUMN 表
     *     3. 后端定义动态列，访问 UI_COLUMN 表
     * </code></pre>
     * 上述配置中 2 和 3 依赖后端配置，且属于此属性的核心信息，其中 3 由于要启用动态列配置，所以还必须启用 zero-ui 模块。
     */
    private KColumn column;


    /**
     * 默认场景下，一个模型的 identifier 应该是文件名，但为了解决模型特殊场景的标识冲突的问题，Zero 框架提供了 identifier 属性，此属性会覆盖文件名，从而解决模型标识冲突的问题。
     */
    @JsonIgnore
    private String identifier;


    /**
     * 连接配置，开启多表模式实现 JOIN 的核心配置，详情参考 {@link KJoin}
     */
    private KJoin connect;     // connect for 1 join 1


    /**
     * 静态模型定义中，若您开发的模块依赖 Jooq 模块，那么此处配置的内容为生成代码部分，之所以称为静态配置，原因在于此处的代码是静态生成的，并非动态配置。
     */
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> daoCls;

    /**
     * 是否要在模型定义中使用 header 属性，几个自定义头如下：
     * <pre><code>
     *     X-App-Id
     *     X-App-Key
     *     X-Lang
     *     X-Tenant-Id
     *     X-Sigma
     * </code></pre>
     */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject header;


    /**
     * 转换配置，用于导入、导出流程中专用，详情参考 {@link KTransform}
     */
    private KTransform transform;

    /**
     * AOP配置，详细配置参考 {@link Aspect} 中的定义和说明
     */
    private JsonObject aop;

    public KField getField() {
        return this.field;
    }

    public void setField(final KField field) {
        this.field = field;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * 切换 identifier 的提取优先级，旧版是以 {@link KColumn} 中为主
     * <pre><code>
     *     - 优先从 identifier 中提取，若无才从 column 中拿
     * </code></pre>
     *
     * @return identifier
     */
    public String identifier() {
        // HAtom 新版本在代码逻辑中填充了 identifier，所以优先处理这种方式的提取方式
        if (Ut.isNotNil(this.identifier)) {
            return this.identifier;
        }


        // CRUD的提取再考虑 column（新版大概率不会走的流程）
        if (Objects.nonNull(this.column)) {
            return this.column.getIdentifier();
        }


        // 默认返回 null
        return null;
    }

    public KModule identifier(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getPojo() {
        return this.pojo;
    }

    public void setPojo(final String pojo) {
        this.pojo = pojo;
    }


    public Class<?> getDaoCls() {
        return this.daoCls;
    }

    public void setDaoCls(final Class<?> daoCls) {
        this.daoCls = daoCls;
    }

    public JsonObject getHeader() {
        return this.header;
    }

    public void setHeader(final JsonObject header) {
        this.header = header;
    }

    public KColumn getColumn() {
        return this.column;
    }

    public void setColumn(final KColumn column) {
        this.column = column;
    }

    public String getTable() {
        Objects.requireNonNull(this.daoCls);
        return JooqPin.initTable(this.daoCls);
    }

    public Class<?> getPojoCls() {
        Objects.requireNonNull(this.daoCls);
        return JooqPin.initPojo(this.daoCls);
    }

    public KJoin getConnect() {
        return this.connect;
    }

    public void setConnect(final KJoin connect) {
        this.connect = connect;
    }

    public KPoint getConnect(final String identifier) {
        if (Objects.isNull(this.connect)) {
            return null;
        }
        final Hymn<String> hymn = Hymn.ofString(this.connect);
        return hymn.pointer(identifier); // this.connect.point(identifier);
    }

    public EmDS.Stored getMode() {
        if (Objects.isNull(this.mode)) {
            return EmDS.Stored.PRIMARY;
        } else {
            return Ut.toEnum(() -> this.mode, EmDS.Stored.class, EmDS.Stored.PRIMARY);
        }
    }

    public void setMode(final EmDS.Stored mode) {
        if (Objects.isNull(mode)) {
            this.mode = EmDS.Stored.PRIMARY.name();
        } else {
            this.mode = mode.name();
        }
    }

    public KTransform getTransform() {
        return this.transform;
    }

    public void setTransform(final KTransform transform) {
        this.transform = transform;
    }

    public String getModeKey() {
        return this.modeKey;
    }

    public void setModeKey(final String modeKey) {
        this.modeKey = modeKey;
    }

    public JsonObject getAop() {
        return this.aop;
    }

    public void setAop(final JsonObject aop) {
        this.aop = aop;
    }

    @Override
    public String toString() {
        return "IxModule{" +
            "name='" + this.name + '\'' +
            ", pojo='" + this.pojo + '\'' +
            ", mode='" + this.mode + '\'' +
            ", modeKey='" + this.modeKey + '\'' +
            ", field=" + this.field +
            ", column=" + this.column +
            ", connect=" + this.connect +
            ", daoCls=" + this.daoCls +
            ", header=" + this.header +
            ", transform=" + this.transform +
            ", aop=" + this.aop +
            '}';
    }
}
