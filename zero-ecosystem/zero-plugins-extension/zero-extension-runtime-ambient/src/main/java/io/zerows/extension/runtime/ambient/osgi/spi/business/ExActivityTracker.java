package io.zerows.extension.runtime.ambient.osgi.spi.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityChangeDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivity;
import io.zerows.extension.skeleton.spi.ExActivity;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExActivityTracker implements ExActivity {
    @Override
    public Future<JsonArray> activities(final String modelId, final String modelKey) {
        return this.fetchActivities(modelId, modelKey)
            .compose(Ux::futureA)
            .compose(Fx.ofJArray(KName.RECORD_NEW, KName.RECORD_OLD));
    }

    @Override
    public Future<JsonArray> activities(final JsonObject condition) {
        return DB.on(XActivityDao.class).fetchAsync(condition).compose(Ux::futureA);
    }

    @Override
    public Future<JsonObject> activity(final String activityId) {
        return DB.on(XActivityDao.class).<XActivity>fetchByIdAsync(activityId).compose(activity -> {
            if (Objects.isNull(activity)) {
                return Ux.futureJ();
            } else {
                return this.changes(activityId).compose(changes -> {
                    final JsonObject activityJ = Ux.toJson(activity);
                    Ut.valueToJObject(activityJ,
                        KName.RECORD_NEW,
                        KName.RECORD_OLD
                    );
                    activityJ.put(KName.CHANGES, changes);
                    return Ux.future(activityJ);
                });
            }
        });
    }

    private Future<List<XActivity>> fetchActivities(final String modelId, final String modelKey) {
        final JsonObject criteria = new JsonObject();
        criteria.put(KName.MODEL_ID, modelId);
        criteria.put(KName.MODEL_KEY, modelKey);
        criteria.put(KName.ACTIVE, Boolean.TRUE);  // active = true
        /*
         * Created By should be synced from system
         * Read all valid activities
         */
        return DB.on(XActivityDao.class).fetchAndAsync(criteria);
    }

    @Override
    public Future<JsonArray> changes(final String modelId, final String modelKey, final String field) {
        Objects.requireNonNull(field);
        return this.fetchActivities(modelId, modelKey).compose(activities -> {
            final Set<String> activityIds = activities.stream()
                .map(XActivity::getKey)
                .collect(Collectors.toSet());
            final JsonObject criteria = new JsonObject();
            criteria.put("activityId,i", Ut.toJArray(activityIds));
            criteria.put("fieldName", field);
            return DB.on(XActivityChangeDao.class).fetchAndAsync(criteria).compose(Ux::futureA);
        });
    }

    @Override
    public Future<JsonArray> changes(final String activityId) {
        return DB.on(XActivityChangeDao.class)
            .fetchAsync(KName.ACTIVITY_ID, activityId)
            .compose(Ux::futureA);
    }
}
