package io.zerows.mbse.metadata;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.component.aop.Aspect;
import io.zerows.component.destine.Hymn;
import io.zerows.epoch.metadata.KField;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.metadata.KTransform;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.apps.KDS;
import io.zerows.platform.enums.EmDS;
import io.zerows.support.Ut;
import lombok.Data;

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
 *     "ds": "数据源名称，默认是 `master`",
 *     "name": "模型名称，此名称会作为 /api/:actor 中的 actor 参数",
 *     "pojo": "（一般转换成遗留系统）当模型启用 Pojo 映射时专用",
 *     "field": "{@link KField}",
 *     "column": "{@link KColumn}"
 * }
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class KModule implements Serializable {
    private String tag;     // OpenAPI 对接
    /**
     * 模块名称，此名称并非模型的 identifier，而是 /api/:actor 中的 actor 参数，最终会转换成 API 定义中的路径，需要注意的是此路径在整个容器环境中都是唯一的，且您的项目
     * 一旦启用了 zero-extension-curd 之后，此名称必须是全容器中唯一的，而且开发人员必须特别熟悉 CRUD 中提供的十五个标准化接口，否则会导致 RESTful 接口规范的冲突。
     */
    private String name;
    /**
     * 针对部分遗留系统，zero 中支持在 /pojo 目录之下存储一层属性映射，映射会存储在 yml 文件中，描述了旧属性和新属性之间的映射关系，这种映射关系会在数据格式转换过程中使用，
     * 且 Zero 框架本身带了这层映射的逻辑，直接配置就可以了。即您不需要做任何开发部分的工作就可以实现模型的属性映射
     */
    private String pojo;
    /**
     * 此处的数据源提取代码：
     * <pre>
     *     {@link io.r2mo.base.dbe.DBMany} 可直接访问此类提取数据源信息
     *     <code class="java>
     *         DBS dbs = DBMany.of().get("数据源名称");
     *         DBS dbs = DBMany.of().get(this.ds);
     *     </code>
     *     此处提取 {@link DBS} 即可，暂不考虑 {@link KDS} 模式，因为 CRUD 仅限静态配置的方式使用，简单说：
     *     - {@link DBMany} 的方式提取的是 static 可共享的数据源相关信息。
     *     - {@link KDS} 则是按照 appId = ?? 可从 X_SOURCE 中提取动态数据源信息（启动时注册到 {@link KDS} 中）。
     *     注：KDS 模式下不可能会有 daoCls，所以当前 KModule 只能绑定 DBS（静态数据源）
     * </pre>
     */
    private String ds;     // mode = EXTENSION
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

    public String getTag() {
        if (StrUtil.isEmpty(tag)) {
            return this.name;
        }
        return this.tag;
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

    public String getTable() {
        Objects.requireNonNull(this.daoCls);
        return DB.on(this.daoCls).metaTable(); // JooqPin.initTable(this.daoCls);
    }

    public Class<?> getPojoCls() {
        Objects.requireNonNull(this.daoCls);
        return DB.on(this.daoCls).metaEntity(); // JooqPin.initPojo(this.daoCls);
    }

    public KJoin.Point getConnect(final String identifier) {
        if (Objects.isNull(this.connect)) {
            return null;
        }
        final Hymn<String> hymn = Hymn.ofString(this.connect);
        return hymn.pointer(identifier); // this.connect.point(identifier);
    }

    @Override
    public String toString() {
        return "IxModule{" +
            "name='" + this.name + '\'' +
            ", pojo='" + this.pojo + '\'' +
            ", ds='" + this.ds + '\'' +
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
