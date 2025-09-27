package io.zerows.module.domain.uca.destine;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.util.Ut;
import io.zerows.module.domain.atom.specification.KJoin;

/**
 * @author lang : 2023-07-31
 */
class ConflateAIo extends ConflateBase<JsonArray, JsonArray> {
    private final transient Conflate<JsonObject, JsonObject> conflate;

    ConflateAIo(final KJoin joinRef, final boolean isOut) {
        super(joinRef);
        if (isOut) {
            // dataIn
            this.conflate = new ConflateJOut(joinRef);
        } else {
            // dataOut
            this.conflate = new ConflateJIn(joinRef);
        }
    }

    /**
     * {@link JsonArray} 数据格式处理，此处
     * <pre><code>
     *     1. active：表示主数据
     *     2. assist：表示输入数据，此处的 assist 会是 active 的超集，即所有的输入属性在 assist 中都会有记录，而
     *                active中会包含所有的 assist 属性。
     * </code></pre>
     *
     * @param active     主数据
     * @param assist     输入数据
     * @param identifier 识别符
     *
     * @return {@link JsonArray} 返回的是一个数组，数组中的每一个元素都是一个 {@link JsonObject}，每一个元素都是
     */
    @Override
    public JsonArray treat(final JsonArray active, final JsonArray assist, final String identifier) {
        final JsonArray zip = new JsonArray();
        // 处理每个元素
        this.procEach(active, assist, identifier, (sourceJ, target) -> {
            // 检查 target
            final JsonObject normalized;
            if (Ut.isNil(target)) {
                normalized = this.conflate.treat(sourceJ, identifier);
            } else {
                normalized = this.conflate.treat(sourceJ, target, identifier);
            }
            zip.add(normalized);
        });
        return zip;
    }

    @Override
    public JsonArray treat(final JsonArray active, final String identifier) {
        throw new _501NotSupportException(this.getClass());
    }
}
