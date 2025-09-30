package io.zerows.module.domain.uca.destine;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.core.uca.log.Log;
import io.zerows.core.util.Ut;
import io.zerows.epoch.runtime.exception._80543Exception412IndentParsing;
import io.zerows.module.domain.atom.specification.KJoin;
import io.zerows.module.domain.atom.specification.KPoint;

import java.util.Objects;

/**
 * 「父主表」
 * 从传入数据中根据定义先提取 identifier，然后根据 identifier 来解析连接点，从配置中提取连接点配置，这种结构主要适用于主从表模式，即一个主表对应多个从表，从主表数据中提取从表的 identifier 标识子模型，有了次标识符之后就可以让主表模型和从表模型执行JOIN操作，配置的数据结构依旧如下：
 * <pre><code>
 *     {
 *         "target": {
 *             "identifier1": {@link KPoint},
 *             "identifier2": {@link KPoint}
 *         }
 *     }
 * </code></pre>
 *
 * @author lang : 2023-07-30
 */
class HymnJObject extends HymnBase<JsonObject> {
    private final Hymn<String> hymnStr;

    HymnJObject(final KJoin joinRef) {
        super(joinRef);
        this.hymnStr = Hymn.ofString(joinRef);
    }

    @Override
    public KPoint pointer(final JsonObject dataJ) {
        // 先从数据节点解析 identifier
        final String identifier = this.id(dataJ);
        Fn.jvmKo(Ut.isNil(identifier), _80543Exception412IndentParsing.class,
            this.joinRef.getTargetIndent(), dataJ.encode());


        // 根据解析到的 identifier 提取连接点
        final KPoint pointer = this.hymnStr.pointer(identifier);
        if (Objects.isNull(pointer)) {
            Log.warn(this.getClass(), "System could not find configuration for `{0}` with data = {1}",
                identifier, dataJ.encode());
        }
        return pointer;
    }
}
