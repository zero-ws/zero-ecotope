package io.zerows.component.destine;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.exception._80548Exception412IndentUnknown;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 「父主表」
 * 从一个数据集中批量提取连接点配置
 * <pre><code>
 *     {
 *         "ofMain": {
 *             "identifier1": {@link KJoin.Point},
 *             "identifier2": {@link KJoin.Point}
 *         }
 *     }
 * </code></pre>
 *
 * @author lang : 2023-07-30
 */
class HymnJArray extends HymnBase<JsonArray> {
    private final Hymn<String> hymnStr;

    HymnJArray(final KJoin joinRef) {
        super(joinRef);
        this.hymnStr = Hymn.ofString(joinRef);
    }

    @Override
    public KJoin.Point pointer(final JsonArray dataA) {
        // 先从数据节点解析 identifier
        final Set<String> idSet = new HashSet<>();
        Ut.itJArray(dataA).map(this::id)
            .filter(Objects::nonNull).forEach(idSet::add);
        Fn.jvmKo(1 != idSet.size(), _80548Exception412IndentUnknown.class, this.joinRef.getTargetIndent());


        // 根据解析到的 identifier 提取连接点
        final String identifier = idSet.iterator().next();
        return this.hymnStr.pointer(identifier);
    }
}
