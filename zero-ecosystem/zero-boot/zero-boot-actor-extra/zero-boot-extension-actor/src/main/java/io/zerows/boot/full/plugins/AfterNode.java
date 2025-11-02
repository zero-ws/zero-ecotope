package io.zerows.boot.full.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.full.base.AbstractAfter;
import io.zerows.boot.graphic.Pixel;
import io.zerows.program.Ux;

/**
 * 图引擎专用处理器
 * 节点后置添加
 */
public class AfterNode extends AbstractAfter {
    @Override
    public Future<JsonObject> afterAsync(final JsonObject record, final JsonObject options) {
        return Pixel.node(this.operation(options), this.atom.identifier()).drawAsync(record)
            .compose(processed -> Ux.future(record));
    }

    @Override
    public Future<JsonArray> afterAsync(final JsonArray records, final JsonObject options) {
        return Pixel.node(this.operation(options), this.atom.identifier()).drawAsync(records)
            .compose(processed -> Ux.future(records));
    }
}
