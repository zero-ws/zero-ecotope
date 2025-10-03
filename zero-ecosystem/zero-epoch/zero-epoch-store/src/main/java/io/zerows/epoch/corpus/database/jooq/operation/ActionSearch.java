package io.zerows.epoch.corpus.database.jooq.operation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.corpus.database.jooq.util.JqAnalyzer;
import io.zerows.epoch.corpus.database.jooq.util.JqFlow;
import io.zerows.component.qr.syntax.Ir;
import io.zerows.epoch.program.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ActionSearch extends AbstractAction {
    private transient final ActionQr qr;
    private transient final AggregatorCount counter;

    ActionSearch(final JqAnalyzer analyzer) {
        super(analyzer);
        // Qr
        this.qr = new ActionQr(analyzer);
        this.counter = new AggregatorCount(analyzer);
    }

    <T> Future<JsonObject> searchAsync(final JsonObject query, final JqFlow workflow) {
        return workflow.inputQrAsync(query).compose(inquiry -> {
            // Search Result
            final Future<JsonArray> dataFuture = this.qr.searchAsync(inquiry)   // action
                .compose(workflow::outputAsync);                            // after : pojo processing
            // Count Result
            final JsonObject criteria = Objects.nonNull(inquiry.getCriteria()) ?
                inquiry.getCriteria().toJson() : new JsonObject();
            final Future<Long> countFuture = this.counter.countAsync(criteria);  // action

            return Future.join(dataFuture, countFuture).compose(result -> {
                // Processing result
                final JsonArray list = result.resultAt(VValue.IDX);
                final Long count = result.resultAt(VValue.ONE);
                // Result here
                return Future.succeededFuture(Ut.valueToPage(list, count));
            });
        }).otherwise(Ut.otherwise(new JsonObject()));
    }

    <T> JsonObject search(final JsonObject query, final JqFlow workflow) {
        // Data Processing
        final Ir qr = workflow.inputQr(query);
        final JsonArray list = workflow.output(this.qr.search(qr));
        // Count Processing
        final Long count = this.counter.count(qr.getCriteria().toJson());
        return Ut.valueToPage(list, count);
    }
}
