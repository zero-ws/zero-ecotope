package io.zerows.module.domain.uca.destine;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.em.EmPRI;
import io.zerows.epoch.common.uca.log.Log;
import io.zerows.core.util.Ut;
import io.zerows.module.domain.atom.specification.KJoin;
import io.zerows.module.domain.atom.specification.KPoint;

import java.util.Objects;

/**
 * 专用抽象类，实际处理过程中会包含内置的 {@link KJoin} 的引用，根据实际定义的连接点元数据信息来解析连接点专用，此处可实现多种不同模式的连接流程，以完善连接点的解析过程。
 *
 * @author lang : 2023-07-28
 */
public abstract class HymnBase<T> implements Hymn<T> {
    /**
     * 构造此对象时，关联的 {@link KJoin} 引用，针对线程级的对象构造，此引用会存储在当前对象中，最终形成 {@link Hymn} 的线程化对象以保持完整对象引用。
     */
    protected final transient KJoin joinRef;

    protected HymnBase(final KJoin joinRef) {
        this.joinRef = joinRef;
    }

    /**
     * 从数据中解析出 identifier，直接根据传入 JSON 数据解析模型标识符 identifier
     * 解析步骤如下：
     * <pre><code>
     *     1. 判断 {@link KJoin#refer()}，查看是 父主表 还是 父从表 模式
     *     2. isRefer = true，父从表模式
     *        这种情况下，直接提取 reference 中定义的 identifier 遵循文件名，如果是 CRUD 模式时
     *        实际 crud 的值就是 identifier。
     *     3. isRefer = false，父主表模式（账单项、会计科目）
     *        检查 targetIndent 是否存在（这个属性必须在这种模式中配置）
     *        - P1：直接根据 targetIndent 从数据中提取属性值，dataJ[targetIndent]
     *        - P2：若不存在 targetIndent，则可以直接使用 targetIndent 作为固定常量
     *        此处的 targetIndent 具有二义性，既可以作为属性名，也可以作为常量值
     * </code></pre>
     * 所以此处存在两个解析，主要根据 isRefer 执行判断
     *
     * @param dataJ {@link JsonObject} 输入的数据记录数据
     *
     * @return {@link String} 解析出来的 identifier
     */
    protected String id(final JsonObject dataJ) {
        if (EmPRI.Connect.PARENT_STANDBY == this.joinRef.refer()) {
            // 父从表模式
            return this.idRefer();
        } else {
            // 父主表模式
            return this.idTarget(dataJ);
        }
    }

    /**
     * 这种模式下不依赖任何输入数据，父从表模式下，父表只可能有一种情况
     * <pre><code>
     * {
     *     "connect": {
     *         "reference": {}
     *     }
     * }
     * </code></pre>
     *
     * @return {@link String} 解析出来的 identifier
     */
    private String idRefer() {
        final KPoint point = this.joinRef.getReference();
        if (Objects.isNull(point)) {
            return null;
        }
        return point.indent();
    }

    /**
     * 这种模式依赖输入数据，父主表模式，父主表模式下需要计算 identifier
     * <pre><code>
     * {
     *     "connect": {
     *         "targetIndent": "identifier",
     *         "target": {
     *             "identifier1": {@link KPoint},
     *             "identifier2": {@link KPoint}
     *         }
     *     }
     * }
     * </code></pre>
     *
     * @param dataJ {@link JsonObject} 输入的数据
     *
     * @return {@link String} 解析出来的 identifier
     */
    private String idTarget(final JsonObject dataJ) {
        final String field = this.joinRef.getTargetIndent();
        if (Ut.isNil(field)) {
            Log.warn(this.getClass(), "The `targetIndent` field is null");
            return null;
        }

        final String identifier;
        if (dataJ.containsKey(field)) {
            // 从数据中解析值
            identifier = dataJ.getString(field);
        } else {
            // 固定值，您的 targetIndent 中配置了什么值就使用什么值
            // 大概率用不上的默认流程
            identifier = field;
        }
        return identifier;
    }
}
