package io.zerows.epoch.bootplus.extension.uca.plugin.semi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.util.Ox;
import io.zerows.epoch.bootplus.extension.scaffold.plugin.AbstractAfter;
import io.zerows.epoch.bootplus.extension.uca.graphic.Pixel;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AfterUdEdgeDelete extends AbstractAfter {
    @Override
    public Future<JsonObject> afterAsync(final JsonObject record, final JsonObject options) {
        final JsonArray normalized = Ox.toLinker(record);
        /*
         * 先删除，再添加
         */
        return Pixel.edge(ChangeFlag.DELETE, this.atom.identifier()).drawAsync(normalized)
            .compose(processed -> Ux.future(record));
    }

    @Override
    public Future<JsonArray> afterAsync(final JsonArray records, final JsonObject options) {
        final JsonArray normalized = Ox.toLinker(records);
        return Pixel.edge(ChangeFlag.DELETE, this.atom.identifier()).drawAsync(normalized)
            .compose(processed -> Ux.future(records));
    }
}
