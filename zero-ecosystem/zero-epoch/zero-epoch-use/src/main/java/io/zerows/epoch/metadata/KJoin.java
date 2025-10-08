package io.zerows.epoch.metadata;

import io.zerows.platform.enums.EmPRI;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 连接配置，位于CRUD模型配置的核心节点 `connect` 的数据结构，结构如下
 * <pre><code>
 * {
 *     "connect": {
 *         "reference": {@link KPoint},
 *         "source": {@link KPoint},
 *         "targetIndent": "提取 identifier 的专用属性名",
 *         "ofMain": {
 *             "identifier1": {@link KPoint},
 *             "identifier2": {@link KPoint}
 *         }
 *     }
 * }
 * </code></pre>
 * 两种模式的 Join 对应的数据结构：
 * <pre><code>
 *     1. 父从表模式
 *        {
 *            "reference",
 *            "source"
 *        }
 *     2. 父主表模式
 *        {
 *            "source",
 *            "targetIndent",
 *            "ofMain"
 *        }
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KJoin implements Serializable {
    /** 做JOIN的目标模型的 identifier 属性 */
    private volatile String targetIndent;

    /** 做JOIN的源相关配置，{@link KPoint} */
    private KPoint source;

    /** 做JOIN的主从表模式下的主从表，{@link KPoint} */
    private KPoint reference;

    /** 做JOIN的目标模型（多个）对应的 {@link KPoint} 对应哈希表配置 **/
    private ConcurrentMap<String, KPoint> target = new ConcurrentHashMap<>();

    public String getTargetIndent() {
        return this.targetIndent;
    }

    public void setTargetIndent(final String targetIndent) {
        this.targetIndent = targetIndent;
    }

    public KPoint getSource() {
        return this.source;
    }

    public void setSource(final KPoint source) {
        this.source = source;
    }

    public KPoint getReference() {
        return this.reference;
    }

    public void setReference(final KPoint reference) {
        this.reference = reference;
    }

    public ConcurrentMap<String, KPoint> getTarget() {
        return this.target;
    }

    public void setTarget(final ConcurrentMap<String, KPoint> target) {
        this.target = target;
    }

    public EmPRI.Connect refer() {
        if (Objects.isNull(this.reference)) {
            // 父主表模式
            return EmPRI.Connect.PARENT_ACTIVE;
        } else {
            // 父从表模式
            return EmPRI.Connect.PARENT_STANDBY;
        }
    }
}
