package io.zerows.extension.runtime.ambient.agent.service;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityChangeDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityChange;
import io.zerows.extension.runtime.ambient.eon.em.ActivityStatus;
import io.zerows.extension.runtime.ambient.osgi.spi.business.ExActivityTracker;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityService implements ActivityStub {
    private static final Cc<String, ExActivity> CC_ACTIVITY = Cc.openThread();

    @Override
    public Future<JsonArray> fetchActivities(final String identifier, final String key) {
        final ExActivity activity = CC_ACTIVITY.pick(ExActivityTracker::new);
        return activity.activities(identifier, key);
    }

    @Override
    public Future<JsonArray> fetchChanges(final String identifier, final String key,
                                          final String field) {
        final ExActivity activity = CC_ACTIVITY.pick(ExActivityTracker::new);
        return activity.changes(identifier, key, field);
    }

    @Override
    public Future<JsonArray> fetchChanges(final String activityId) {
        final ExActivity activity = CC_ACTIVITY.pick(ExActivityTracker::new);
        return activity.changes(activityId);
    }

    @Override
    public Future<JsonObject> fetchActivity(final String id) {
        final ExActivity activity = CC_ACTIVITY.pick(ExActivityTracker::new);
        return activity.activity(id);
    }

    @Override
    public Future<JsonArray> saveChanges(final String id, final ActivityStatus status) {
        final UxJooq jq = Ux.Jooq.on(XActivityChangeDao.class);
        return jq.<XActivityChange>fetchAsync(KName.ACTIVITY_ID, id).compose(changes -> {
            final List<XActivityChange> original = new ArrayList<>(changes);
            Ut.itList(original, (change, index) -> {
                final String oldStatus = change.getStatus();
                final XActivityChange itemRef = changes.get(index);
                if (Ut.isNil(oldStatus)) {
                    itemRef.setStatus(status.name());
                } else {
                    if (ActivityStatus.CONFIRMED == status) {
                        /*
                         * -> CONFIRMED
                         * Only `PENDING` allowed, system keeped
                         */
                        final ActivityStatus old =
                            Ut.toEnum(change::getStatus, ActivityStatus.class, ActivityStatus.SYSTEM);
                        if (ActivityStatus.PENDING == old) {
                            itemRef.setStatus(ActivityStatus.CONFIRMED.name());
                        }
                    } else {
                        /*
                         * -> PENDING or SYSTEM
                         * It's allowed directly
                         */
                        itemRef.setStatus(status.name());
                    }

                }
            });
            return jq.updateAsync(changes).compose(Ux::futureA);
        });
    }
}
