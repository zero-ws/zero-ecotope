package io.zerows.epoch.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.function.Fn;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.exception._80542Exception409JoinTarget;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.platform.metadata.KMapping;
import io.zerows.support.Ut;

import java.io.Serializable;
import java.util.Objects;

/**
 * 连接点专用配置，所有连接点配置一致 {@link KPoint} ，都是固定数据结构：
 * <pre><code>
 * {
 *     "identifier": "连接点的唯一标识符，通常是模型名",
 *     "crud": "{@link EmModel.Join#CRUD} 模式必须：crud join 模式必须的，使用 CURD 标准定义执行模式连接",
 *     "classDao": "{@link EmModel.Join#DAO} 模式必须：直连模式",
 *     "classDefine": "{@link EmModel.Join#DEFINE} 模式必须：定义模式",
 *     "key": "当前模型的主键是什么",
 *     "keyJoin": "当前模型的的连接键是什么",
 *     "synonym": {
 *         "field": "field alias"
 *     }
 * }
 * </code></pre>
 * 上述连接点专用属性 synonym 需要说明一下，当两个表连接时，如果两个表中存在相同的字段名，那么在连接时会出现冲突，所以需要
 * 使用此属性重新定义某张表中的属性别名，保证连接时不会冲突。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KPoint implements Serializable {
    /** 模型标识符 */
    @JsonIgnore
    private String identifier;


    /** {@link EmModel.Join#CRUD} 专用：可解析的crud连接专用文件 */
    private String crud;


    /** {@link EmModel.Join#DAO} 专用：Java的类名，做直连JOIN专用 */
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classDao;


    /** {@link EmModel.Join#DEFINE} 专用： **/
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classDefine;


    /** 主键名 **/
    private String key;

    /** 连接键名 **/
    private String keyJoin;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject synonym;

    public String getCrud() {
        return this.crud;
    }

    public void setCrud(final String crud) {
        this.crud = crud;
    }

    public String getKey() {
        return Ut.isNil(this.key) ? "key" : this.key;
    }

    public void setKey(final String key) {
        if (Ut.isNotNil(key)) {
            this.key = key;
        }
    }

    public String getKeyJoin() {
        return this.keyJoin;
    }

    public void setKeyJoin(final String keyJoin) {
        this.keyJoin = keyJoin;
    }

    public Class<?> getClassDao() {
        return this.classDao;
    }

    public void setClassDao(final Class<?> classDao) {
        this.classDao = classDao;
    }

    public Class<?> getClassDefine() {
        return this.classDefine;
    }

    public void setClassDefine(final Class<?> classDefine) {
        this.classDefine = classDefine;
    }

    public JsonObject getSynonym() {
        return Objects.isNull(this.synonym) ? new JsonObject() : this.synonym;
    }

    public void setSynonym(final JsonObject synonym) {
        this.synonym = synonym;
    }

    public KMapping synonym() {
        return new KMapping(this.synonym);
    }


    // ------------------ 模式解析

    /**
     * 获取目标JOIN模式的相关配置，主要有三种模式，按优先级排序
     * <pre><code>
     *     - P1: （Extension扩展）优先考虑是否 CRUD 模式（模型定义）
     *           这种模式下 zero-crud 中保存了当前环境中所有合法的 KModule 模型定义库
     *           等价于 crud = KModule，这种模式下系统会根据 crud 的定义去提取 KModule
     *           - 直接使用 identifier 提取（crud = identifier）
     *           - 使用别名，{} 中定义的 name -> identifier = KModule，这种模式中 name 就是别名
     *     - P2：（Jooq纯模式）若非 CRUD，则直接考虑是否直接定义了 classDao
     *           这种模式相当于模型本身是游离的，不存储在 zero-crud 中，而是直接通过 Jooq 的直接
     *           定义来做相关关联，这种模式下模型本体不会被 zero-crud 扩展捕捉，所以可应用于不启用
     *           CRUD 的场景。
     *     - P3：（用户自定义模式）最低优先级的模式，这种模式下系统会直接使用 classDefine 来实现，
     *           这种模式属于自定义模式，保留后期使用。
     * </code></pre>
     *
     * @return {@link EmModel.Join}
     */
    public EmModel.Join modeTarget() {
        /* P1: CRUD */
        if (Ut.isNotNil(this.crud)) {
            return EmModel.Join.CRUD;
        }
        /* P2: classDao */
        if (Objects.nonNull(this.classDao)) {
            return EmModel.Join.DAO;
        }
        /* P3: classDefine also null, throw error out. */
        Fn.jvmKo(Objects.isNull(this.classDefine), _80542Exception409JoinTarget.class);
        return EmModel.Join.DEFINE;
    }

    /**
     * JOIN 源处理，由于源部分会优先填充 classDao 变量（当前模型中会直接定义），也不存在引用，所以
     * 可以直接切换优先级来处理
     * <pre><code>
     *     - P1：（Jooq纯模式）直接考虑 classDao 定义
     *           这种模式即使是在 zero-crud 中定义的模型也会默认被设置此属性，所以此处优先级最高
     *           简单说 源处理本质上是不存在 CRUD 模式的，因为源部分不会存在引用。
     *     - P2：（用户自定义模式）次低优先级就是用户自定义模式，保留后期使用。
     *     - P3：（Extension扩展）这种模式作为最低优先级，注释中已经解释过了。
     * </code></pre>
     *
     * @return {@link EmModel.Join}
     */
    public EmModel.Join modeSource() {
        /* P1: classDao */
        if (Objects.nonNull(this.classDao)) {
            return EmModel.Join.DAO;
        }
        /* P2: classDefine */
        if (Objects.nonNull(this.classDefine)) {
            return EmModel.Join.DEFINE;
        }
        /* P3: keyJoin */
        Fn.jvmKo(Ut.isNil(this.keyJoin), _80542Exception409JoinTarget.class);
        return EmModel.Join.CRUD;
    }

    /**
     * 设置 identifier，此处比较特殊的是 CRUD 模型，在当前的环境中 crud / identifier 两个值暂时是维持同步状态
     * <pre><code>
     *     1. 直接设置 identifier = 输入的 identifier
     *     2. crud 的设置会考虑配置结果
     *        - 若没有配置 crud，则将 crud 设置成 identifier
     *        - 若已经配置了 crud，证明 crud 和 identifier 不一样（别名切换）
     * </code></pre>
     *
     * @param identifier 模型标识符
     *
     * @return {@link KPoint} 当前连接点引用
     */
    public KPoint indent(final String identifier) {
        this.identifier = identifier;
        if (Objects.isNull(this.crud)) {
            // Default Applying
            this.crud = identifier;
        }
        return this;
    }

    /**
     * 读取模型标识符，此处读取模型标识符会存在一种情况，就是没有设置 identifier
     * <pre><code>
     *     1. 优先读取 identifier 属性（只能依靠设置，不能依靠配置）
     *     2. 无法提取 identifier 时，则智能查找 crud 查看是否存在系统指定的模型标识符
     * </code></pre>
     * 简单说若是动态建模或扩展建模时，您不应该依赖系统的行为来处理，而是使用自己设置的 identifier。
     *
     * @return 模型标识符
     */
    public String indent() {
        return Ut.isNil(this.identifier) ? this.crud : this.identifier;
    }

    @Override
    public String toString() {
        return "KPoint{" +
            "identifier='" + this.identifier + '\'' +
            ", crud='" + this.crud + '\'' +
            ", classDao=" + this.classDao +
            ", classDefine=" + this.classDefine +
            ", key='" + this.key + '\'' +
            ", keyJoin='" + this.keyJoin + '\'' +
            ", synonym=" + this.synonym +
            '}';
    }
}
