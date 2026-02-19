package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.daos.XSubscriptionDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSubscription;
import io.zerows.extension.module.ambient.servicespec.SubscriptionStub;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;

/**
 * Subscription Special Operations Service Implementation
 *
 * @author lang : 2026-02-14
 */
public class SubscriptionService implements SubscriptionStub {

    @Override
    public Future<JsonObject> fetchDashboard(final String sigma) {
        if (Ut.isNil(sigma)) {
            return Ux.future(new JsonObject());
        }

        // 查询该 sigma 下的所有订阅
        final JsonObject criteria = Ux.whereAnd();
        criteria.put("sigma", sigma);
        criteria.put("active", Boolean.TRUE);

        return DB.on(XSubscriptionDao.class).<XSubscription>fetchAsync(criteria).compose(subscriptions -> {
            final JsonObject dashboard = new JsonObject();
            final LocalDateTime now = LocalDateTime.now();

            // 统计数据
            int totalCount = subscriptions.size();
            int activeCount = 0;
            int expiringSoonCount = 0; // 30天内到期
            int expiredCount = 0;

            for (final XSubscription sub : subscriptions) {
                if ("ACTIVE".equals(sub.getStatus())) {
                    activeCount++;

                    // 检查是否即将到期
                    if (sub.getEndAt() != null) {
                        if (sub.getEndAt().isBefore(now)) {
                            expiredCount++;
                        } else if (sub.getEndAt().isBefore(now.plusDays(30))) {
                            expiringSoonCount++;
                        }
                    }
                }
            }

            dashboard.put("totalSubscriptions", totalCount);
            dashboard.put("activeSubscriptions", activeCount);
            dashboard.put("expiringSoon", expiringSoonCount);
            dashboard.put("expired", expiredCount);
            dashboard.put("timestamp", now.toString());

            return Ux.future(dashboard);
        });
    }

    @Override
    public Future<JsonObject> cancelSubscription(final String subId) {
        if (Ut.isNil(subId)) {
            return Ux.future(new JsonObject().put("error", "Subscription ID is required"));
        }

        return DB.on(XSubscriptionDao.class).<XSubscription>fetchByIdAsync(subId).compose(subscription -> {
            if (subscription == null) {
                return Ux.future(new JsonObject().put("error", "Subscription not found"));
            }

            // 更新状态为 CANCELLED
            subscription.setStatus("CANCELLED");
            subscription.setActive(false);
            subscription.setUpdatedAt(LocalDateTime.now());

            // 记录取消历史到 metadata
            final JsonObject metadata = subscription.getMetadata() == null ? new JsonObject() : subscription.getMetadata();
            final JsonArray cancelLog = metadata.getJsonArray("cancelLog", new JsonArray());
            cancelLog.add(new JsonObject()
                .put("action", "CANCEL")
                .put("timestamp", LocalDateTime.now().toString())
                .put("reason", "User cancelled"));
            metadata.put("cancelLog", cancelLog);
            subscription.setMetadata(metadata);

            return DB.on(XSubscriptionDao.class).updateAsync(subscription).compose(Ux::futureJ);
        });
    }

    @Override
    public Future<JsonObject> renewSubscription(final String subId) {
        if (Ut.isNil(subId)) {
            return Ux.future(new JsonObject().put("error", "Subscription ID is required"));
        }

        return DB.on(XSubscriptionDao.class).<XSubscription>fetchByIdAsync(subId).compose(subscription -> {
            if (subscription == null) {
                return Ux.future(new JsonObject().put("error", "Subscription not found"));
            }

            // 延长订阅时间（根据 renewType）
            final LocalDateTime currentEnd = subscription.getEndAt();
            LocalDateTime newEnd = currentEnd;

            if ("MONTHLY".equals(subscription.getRenewType())) {
                newEnd = currentEnd.plusMonths(1);
            } else if ("YEARLY".equals(subscription.getRenewType())) {
                newEnd = currentEnd.plusYears(1);
            } else if ("QUARTERLY".equals(subscription.getRenewType())) {
                newEnd = currentEnd.plusMonths(3);
            }

            subscription.setEndAt(newEnd);
            subscription.setStatus("ACTIVE");
            subscription.setActive(true);
            subscription.setUpdatedAt(LocalDateTime.now());

            // 记录续费历史到 metadata
            final JsonObject metadata = subscription.getMetadata() == null ? new JsonObject() : subscription.getMetadata();
            final JsonArray renewLog = metadata.getJsonArray("renewLog", new JsonArray());
            renewLog.add(new JsonObject()
                .put("action", "RENEW")
                .put("timestamp", LocalDateTime.now().toString())
                .put("oldEndAt", currentEnd.toString())
                .put("newEndAt", newEnd.toString())
                .put("renewType", subscription.getRenewType()));
            metadata.put("renewLog", renewLog);
            subscription.setMetadata(metadata);

            return DB.on(XSubscriptionDao.class).updateAsync(subscription).compose(Ux::futureJ);
        });
    }
}
