package io.zerows.component.destine;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.metadata.specification.KJoin;
import io.zerows.epoch.corpus.metadata.specification.KPoint;
import io.zerows.platform.enums.EmPRI;
import io.zerows.epoch.program.Ut;

import java.util.Objects;

/**
 * @author lang : 2023-07-31
 */
class ConflateJOut extends ConflateBase<JsonObject, JsonObject> {
    ConflateJOut(final KJoin joinRef) {
        super(joinRef);
    }

    /**
     * 输出会包含两部分数据
     * <pre><code>
     *     1. active：主数据（使用 `key` ）
     *     2. assist：连接部分数据（使用 `keyJoin` ）
     * </code></pre>
     *
     * @param active     主数据
     * @param assist     连接部分数据
     * @param identifier 被连接的 identifier
     *
     * @return 处理完成后数据
     */
    @Override
    public JsonObject treat(final JsonObject active, final JsonObject assist, final String identifier) {
        // 提取初始数据，数据执行基础 standJ ( assist )
        final JsonObject standJ = Ut.valueJObject(assist, true);


        // 同义词（Synonym）
        final KPoint point = this.target(identifier);
        if (Objects.nonNull(point)) {
            final JsonObject synonymJ = Ut.aiOut(standJ, point.synonym(), false);
            standJ.mergeIn(synonymJ, true);
        }


        // 连接点数据
        final JsonObject combine = standJ.mergeIn(active, true);
        if (EmPRI.Connect.PARENT_ACTIVE == this.joinRef.refer()) {
            // 这个代码只在父主表模式生效
            final JsonObject connected = this.procOutput(combine, identifier);
            combine.mergeIn(connected, true);
        }

        // 构造最终数据
        return combine;
    }

    @Override
    public JsonObject treat(final JsonObject active, final String identifier) {
        // 获取连接部分数据
        final JsonObject data = Ut.valueJObject(active, true);


        // 根据连接点构造数据返回
        return this.procOutput(data, identifier);
    }
}
