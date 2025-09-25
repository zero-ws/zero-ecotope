package io.zerows.core.web.mbse.atom.specification;

import io.zerows.core.util.Ut;

import java.io.Serializable;

/**
 * 当 zero-ui 和 zero-crud 合并处理时专用，处理模型中的列表列定义专用。
 * <pre><code>
 *     {
 *         "column": {
 *             "dynamic": "true / false，动态列还是静态列，可自动计算",
 *             "identifier": "若此值为 null，则表示动态列",
 *             "view": "视图名称，默认为 DEFAULT"
 *         }
 *     }
 * </code></pre>
 * 此处配置仅在列表提取时会使用，Zero Extension 中提取List列配置有三种方案
 * <pre><code>
 *     1. 直接在前端使用 InJson 文件的方式配置（资源文件中）
 *     2. 后端不访问 zero-ui 中的数据库定义，直接使用静态 InJson 文件，这种方式就是 {@link KColumn} 做连接时的配置。
 *     3. 后端直接访问 zero-ui 模块中 `UI_COLUMN` 表中的定义来提取（纯动态模式）
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KColumn implements Serializable {
    /** 动态配置开关，若是动态列则 dynamic = true */
    private transient Boolean dynamic = Boolean.FALSE;

    /** 开启动态列的条件，identifier = null，否则直接从 JSON 文件中提取 */
    private transient String identifier;

    /** 视图名称，和安全视图提取相关 **/
    private transient String view = "DEFAULT";  // Default name

    public Boolean getDynamic() {
        /* Basic calculation for column analyze mode */
        this.dynamic = Ut.isNil(this.identifier);
        return this.dynamic;
    }

    public void setDynamic(final Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getView() {
        return this.view;
    }

    public void setView(final String view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "IxColumn{" +
            "dynamic=" + this.dynamic +
            ", identifier='" + this.identifier + '\'' +
            ", view='" + this.view + '\'' +
            '}';
    }

}
