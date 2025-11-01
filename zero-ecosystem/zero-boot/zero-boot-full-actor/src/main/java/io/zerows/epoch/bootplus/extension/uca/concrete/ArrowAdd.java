package io.zerows.epoch.bootplus.extension.uca.concrete;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ArrowAdd extends ArrowBase {
    @Override
    public Future<JsonObject> processAsync(final JsonObject record) {
        LOG.Uca.info(this.getClass(), "（单）插入数据：identifier = {0}, data = {1}",
            this.identifier(), record.encode());
        return this.dao().insertAsync(this.record(record)).compose(Ux::futureJ);
    }

    @Override
    public Future<JsonArray> processAsync(final JsonArray records) {
        LOG.Uca.info(this.getClass(), "（批）插入数据：identifier = {0}, data = {1}",
            this.identifier(), records.encode());
        return this.dao().insertAsync(this.records(records)).compose(Ux::futureA);
    }
}
