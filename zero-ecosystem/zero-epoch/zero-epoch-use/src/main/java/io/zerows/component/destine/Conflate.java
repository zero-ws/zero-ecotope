package io.zerows.component.destine;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.metadata.KPoint;
import io.zerows.platform.exception._60050Exception501NotSupport;

/**
 * 包域，不对外
 * 根据 {@link KPoint} 计算对应的组件，处理三个生命周期
 * <pre><code>
 *     1. 输入数据处理
 *     2. 输出数据处理
 *     3. 连接点处理
 * </code></pre>
 * 此处为了处理原版的几个特殊方法。
 *
 * @author lang : 2023-07-30
 */
@SuppressWarnings("all")
public interface Conflate<I, O> {

    // ( JsonArray | JsonObject ) / JsonObject
    static <I> Conflate<I, JsonObject> ofQr(final KJoin joinRef, final boolean isArray) {
        return BuilderConflate.ofQr(joinRef, isArray);
    }

    // JsonArray / JsonArray
    static Conflate<JsonArray, JsonArray> ofJArray(final KJoin joinRef, final boolean isOut) {
        return BuilderConflate.ofJArray(joinRef, isOut);
    }

    // JsonObject / JsonObject
    static Conflate<JsonObject, JsonObject> ofJObject(final KJoin joinRef, final boolean isOut) {
        return BuilderConflate.ofJObject(joinRef, isOut);
    }

    // 默认实现是为了 QR 部分量身打造，不可直接使用
    default O treat(final I active, final I assist, final String identifier) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    O treat(final I active, final String identifier);
}
