package io.zerows.component.destine;

import io.zerows.epoch.metadata.KJoin;
import io.zerows.platform.enums.EmDS;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 「父主表」
 * 传入参数数据类型为 String 类型，即根据模型 identifier 来解析连接点，从配置中提取连接点配置，对应配置数据结构如下：
 * <pre><code>
 *     {
 *         "ofMain": {
 *             "identifier1": {@link KJoin.Point},
 *             "identifier2": {@link KJoin.Point}
 *         }
 *     }
 * </code></pre>
 * 此处在配置连接时，identifier 为统一模型标识符。
 *
 * @author lang : 2023-07-28
 */
class HymnString extends HymnBase<String> {
    HymnString(final KJoin joinRef) {
        super(joinRef);
    }

    @Override
    public KJoin.Point pointer(final String identifier) {
        if (Objects.isNull(identifier)) {
            // 输入为 null，直接返回 null 连接点
            return null;
        }
        if (EmDS.Connect.PARENT_STANDBY == this.joinRef.refer()) {
            // 父从表模式
            return this.pointRefer(identifier);
        } else {
            // 父主表模式
            return this.pointTarget(identifier);
        }
    }

    private KJoin.Point pointTarget(final String identifier) {
        final ConcurrentMap<String, KJoin.Point> targetMap = this.joinRef.getTarget();
        final KJoin.Point point = targetMap.getOrDefault(identifier, null);
        if (Objects.isNull(point)) {
            // 目标连接点为 null，直接返回
            return null;
        }
        /*
         * 连接点查找到之后，针对连接点执行数据设置，此处设置两个属性
         * - identifier: 将 identifier 设置成当前传入的模型标识符
         * - crud: 若 crud 的值为空，则直接设置当前 crud 属性的值和 identifier 绑定
         * 第二种情况主要用于如下
         * - crud / actor：相同时，则直接 identifier 和 actor 是一致的（都为 identifier）
         * - crud / actor：不同时，则直接 identifier 和 actor 是不一致的
         */
        return point.indent(identifier);
    }

    private KJoin.Point pointRefer(final String identifier) {
        final KJoin.Point point = this.joinRef.getReference();
        if (Objects.isNull(point)) {
            // 目标连接点为 null，直接返回
            return null;
        }
        return point.indent(identifier);
    }
}
