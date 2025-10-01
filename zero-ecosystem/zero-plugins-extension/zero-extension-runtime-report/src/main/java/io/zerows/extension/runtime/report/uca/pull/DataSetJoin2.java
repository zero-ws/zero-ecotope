package io.zerows.extension.runtime.report.uca.pull;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.shared.program.Kv;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.configuration.module.modeling.MDConnect;
import io.zerows.epoch.corpus.database.jooq.operation.UxJoin;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.epoch.program.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-10-22
 */
class DataSetJoin2 extends AbstractDataSet {
    protected final MDConnect active;
    protected final MDConnect standBy;
    protected final JsonObject children = new JsonObject();
    protected final Kv<String, String> kvJoin;
    protected final JsonObject aliasJ = new JsonObject();

    DataSetJoin2(final JsonObject sourceJ) {
        final String active = Ut.valueString(sourceJ, KName.ACTIVE);
        this.active = HExtension.connect(active);
        Objects.requireNonNull(this.active);
        final String standBy = Ut.valueString(sourceJ, "standby");
        this.standBy = HExtension.connect(standBy);
        Objects.requireNonNull(this.standBy);
        this.children.mergeIn(Ut.valueJObject(sourceJ, KName.CHILDREN));

        final String activeField = Ut.valueString(sourceJ, "active.field", KName.KEY);
        final String standbyField = Ut.valueString(sourceJ, "standby.field", KName.KEY);
        this.kvJoin = Kv.create(activeField, standbyField);

        this.aliasJ.mergeIn(Ut.valueJObject(sourceJ, KName.ALIAS));
    }

    @Override
    public Future<JsonArray> loadAsync(final JsonObject params, final JsonObject queryJ) {
        // final JsonObject queryJ = Ut.toJObject(this.dataSet.getDataQuery());
        final JsonObject parameters = Tool.inputParameter(params, queryJ);
        if (Ut.isNil(parameters)) {
            return Ux.futureA();
        }
        this.logger().info("Report processing for Active = {}, StandBy = {}, Cond: {}",
            this.active.getTable(), this.standBy.getTable(), parameters.encode());
        // 提取 UxJoin
        final UxJoin jq = Ux.Join.bridge(this.active, this.standBy, this.kvJoin, this.aliasJ);

        return jq.fetchAsync(parameters).compose(data -> this.loadChildren(data, this.children));
    }
}
