package io.zerows.component.destine;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2023-07-30
 */
class ConflateJIn extends ConflateBase<JsonObject, JsonObject> {
    ConflateJIn(final KJoin joinRef) {
        super(joinRef);
    }

    /**
     * 输入会包含两部分数据
     * <pre><code>
     *     1. active：表示主数据
     *     2. assist：此时表示请求中的输入数据
     * </code></pre>
     * 此处的 `key` 来自于 active 数据生成 `joinKey = get` 到最终数据部分
     *
     * @param active     主数据
     * @param assist     输入数据
     * @param identifier 被连接的 identifier
     *
     * @return 处理完成后数据
     */
    @Override
    public JsonObject treat(final JsonObject active, final JsonObject assist, final String identifier) {
        // 提取初始数据，数据执行基础 inputJ ( assist ) + active
        final JsonObject inputJ = Ut.valueJObject(assist, true);
        if (Ut.isNotNil(active)) {
            inputJ.mergeIn(active, true);
        }


        // 提取连接点数据（Joined Key）
        final JsonObject connected = this.procInput(inputJ, identifier);
        inputJ.mergeIn(connected, true);


        // 同义词（Synonym）
        final KJoin.Point point = this.target(identifier);
        if (Objects.nonNull(point)) {
            final JsonObject synonymJ = Ut.aiIn(inputJ, point.synonym(), false);
            inputJ.mergeIn(synonymJ, true);
        }

        // 构造最终数据
        return inputJ;
    }

    /**
     * 这种场景下仅有 active 部分的数据是合法的，这种情况下 synonym 配置是无效的，别名映射配置仅限于在超过两张表做 JOIN 时会生效或使用，所以此API仅适用于单独数据部分的处理
     *
     * @param active     主数据
     * @param identifier 模型标识符
     *
     * @return 返回固定
     */
    @Override
    public JsonObject treat(final JsonObject active, final String identifier) {
        // 获取连接部分数据
        final JsonObject data = Ut.valueJObject(active, true);


        // 根据连接点构造数据返回输入部分
        return this.procInput(data, identifier);
    }
}
