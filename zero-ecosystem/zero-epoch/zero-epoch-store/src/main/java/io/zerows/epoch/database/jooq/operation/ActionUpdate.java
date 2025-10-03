package io.zerows.epoch.database.jooq.operation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.database.jooq.util.JqAnalyzer;
import org.jooq.Query;
import org.jooq.UpdateConditionStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
class ActionUpdate extends AbstractAction {

    private transient ActionFetch fetch;

    ActionUpdate(final JqAnalyzer analyzer) {
        super(analyzer);
        // Qr
        this.fetch = new ActionFetch(analyzer);
    }

    // ============ UPDATE Operation =============

    /* Future<Tool> */
    <T> Future<T> updateAsync(final T entity) {
        Objects.requireNonNull(entity);
        return ((Future<Integer>) this.dao().update(entity)).compose(rows -> {
            this.logging("[ Jq ] updateAsync(Tool) executed rows: {0}", String.valueOf(rows));
            return Future.succeededFuture(entity);
        });
    }

    /* Tool */
    <T> T update(final T entity) {
        Objects.requireNonNull(entity);
        final UpdateConditionStep updateStep = this.editRecord(entity);
        final int rows = updateStep.execute();
        this.logging("[ Jq ] update(Tool) executed rows: {0}", String.valueOf(rows));
        return entity;
    }

    /* Future<List<Tool>> */
    <T> Future<List<T>> updateAsync(final List<T> list) {
        return this.dsl.executeBlocking(h -> h.complete(this.update(list)));
    }

    /* Tool */
    <T> List<T> update(final List<T> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            this.logging("[ Jq ] update(List<Tool>) executed empty: 0");
            return list;
        }
        final List<Query> batchOps = new ArrayList<>();
        list.stream().map(this::editRecord).forEach(batchOps::add);
        final int rows[] = this.context().batch(batchOps).execute();
        final long updated = Arrays.stream(rows).filter(value -> VValue.ONE == value).count();
        this.logging("[ Jq ] update(List<Tool>) executed rows: {0}/{1}",
            String.valueOf(updated), String.valueOf(rows.length));
        return list;
    }

    /* Future<Tool> */
    <T, ID> Future<T> updateAsync(final ID id, final T updated) {
        return this.fetch.fetchByIdAsync(id).compose(previous -> {
            final T combine = this.analyzer.copyEntity((T) previous, updated);
            return this.updateAsync(combine);
        });
    }

    /* Tool */
    <T, ID> T update(final ID id, final T updated) {
        final T previous = this.fetch.fetchById(id);
        final T combine = this.analyzer.copyEntity((T) previous, updated);
        return this.update(combine);
    }

    /* Future<Tool> */
    <T> Future<T> updateAsync(final JsonObject criteria, final T updated) {
        return this.fetch.fetchOneAsync(criteria).compose(previous -> {
            final T combine = this.analyzer.copyEntity((T) previous, updated);
            return this.updateAsync(combine);
        });
    }

    /* Tool */
    <T> T update(final JsonObject criteria, final T updated) {
        final T previous = this.fetch.fetchOne(criteria);
        final T combine = this.analyzer.copyEntity((T) previous, updated);
        return this.update(combine);
    }
}
