package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.daos.XSubscriptionDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSubscription;
import io.zerows.extension.module.ambient.servicespec.SubscriptionStub;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Subscription Special Operations Service Implementation
 *
 * @author lang : 2026-02-14
 */
public class SubscriptionService implements SubscriptionStub {

    @Override
    public Future<JsonObject> purchaseApp(final String tenantId, final String sigma, final JsonObject input) {
        if (Ut.isNil(tenantId)) {
            return Ux.future(this.error("Tenant ID is required"));
        }
        if (input == null || Ut.isNil(input.getString(KName.APP_ID))) {
            return Ux.future(this.error("Application ID is required"));
        }
        final String appId = input.getString(KName.APP_ID);
        final String operatorId = input.getString("operatorId");
        return DB.on(XAppDao.class).<XApp>fetchByIdAsync(appId).compose(app -> {
            if (Objects.isNull(app)) {
                return Ux.future(this.error("Application not found"));
            }
            final JsonObject criteria = Ux.whereAnd();
            criteria.put(KName.TENANT_ID, tenantId);
            criteria.put(KName.APP_ID, appId);
            return DB.on(XSubscriptionDao.class).<XSubscription>fetchAsync(criteria).compose(subscriptions -> {
                final LocalDateTime now = LocalDateTime.now();
                final XSubscription current = this.pickCurrent(subscriptions);
                if (Objects.nonNull(current)) {
                    if (Boolean.TRUE.equals(current.getActive()) && "ACTIVE".equalsIgnoreCase(current.getStatus())) {
                        return Ux.future(this.toPurchasedItem(current, app));
                    }
                    final XSubscription activated = this.activateSubscription(
                        current, tenantId, sigma, app, input, operatorId, now
                    );
                    return DB.on(XSubscriptionDao.class)
                        .updateAsync(activated)
                        .compose(saved -> Ux.future(this.toPurchasedItem(saved, app)));
                }

                final XSubscription created = this.createSubscription(
                    tenantId, sigma, app, input, operatorId, now
                );
                return DB.on(XSubscriptionDao.class)
                    .insertAsync(created)
                    .compose(saved -> Ux.future(this.toPurchasedItem(saved, app)));
            });
        });
    }

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
    public Future<JsonArray> fetchMySubscriptions(final String userId) {
        if (Ut.isNil(userId)) {
            return Ux.future(new JsonArray());
        }

        // 个人订阅：按 createdBy（用户ID）查询
        final JsonObject criteria = Ux.whereAnd();
        criteria.put("createdBy", userId);

        return DB.on(XSubscriptionDao.class).<XSubscription>fetchAsync(criteria).compose(subscriptions -> {
            final JsonArray result = new JsonArray();
            final LocalDateTime now = LocalDateTime.now();

            for (final XSubscription sub : subscriptions) {
                final JsonObject item = Ux.toJson(sub);

                // 计算订阅状态
                String displayStatus = sub.getStatus();
                if ("ACTIVE".equals(sub.getStatus()) && sub.getEndAt() != null) {
                    if (sub.getEndAt().isBefore(now)) {
                        displayStatus = "EXPIRED";
                    } else if (sub.getEndAt().isBefore(now.plusDays(7))) {
                        displayStatus = "EXPIRING_SOON";
                    }
                }
                item.put("displayStatus", displayStatus);

                // 计算剩余天数
                if (sub.getEndAt() != null) {
                    long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(now, sub.getEndAt());
                    item.put("daysRemaining", Math.max(0, daysRemaining));
                }

                result.add(item);
            }

            return Ux.future(result);
        });
    }

    @Override
    public Future<JsonObject> searchPurchased(final String tenantId, final JsonObject query) {
        if (Ut.isNil(tenantId)) {
            return Ux.future(this.emptyPurchased());
        }

        final JsonObject criteria = Ux.whereAnd();
        criteria.put(KName.TENANT_ID, tenantId);
        return DB.on(XSubscriptionDao.class).<XSubscription>fetchAsync(criteria).compose(subscriptions -> {
            if (subscriptions.isEmpty()) {
                return Ux.future(this.emptyPurchased());
            }
            subscriptions.sort(this::compareSubscriptionDesc);
            return this.fetchPurchasedItems(subscriptions, 0, new ArrayList<>()).compose(items -> {
                final List<JsonObject> filtered = this.filterPurchased(items, query);
                final JsonArray paged = this.paginatePurchased(filtered, query);
                return Ux.future(new JsonObject()
                    .put("list", paged)
                    .put("count", filtered.size()));
            });
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

    private Future<List<JsonObject>> fetchPurchasedItems(
        final List<XSubscription> subscriptions,
        final int index,
        final List<JsonObject> items
    ) {
        if (index >= subscriptions.size()) {
            return Ux.future(items);
        }

        final XSubscription subscription = subscriptions.get(index);
        final String appId = subscription.getAppId();
        if (Ut.isNil(appId)) {
            items.add(this.toPurchasedItem(subscription, null));
            return this.fetchPurchasedItems(subscriptions, index + 1, items);
        }
        return DB.on(XAppDao.class).<XApp>fetchByIdAsync(appId).compose(app -> {
            items.add(this.toPurchasedItem(subscription, app));
            return this.fetchPurchasedItems(subscriptions, index + 1, items);
        });
    }

    private List<JsonObject> filterPurchased(final List<JsonObject> items, final JsonObject query) {
        final String keyword = this.keyword(query);
        if (Ut.isNil(keyword)) {
            return items;
        }

        final String normalized = keyword.toLowerCase(Locale.ROOT);
        final List<JsonObject> filtered = new ArrayList<>();
        items.forEach(item -> {
            final String title = item.getString("title", "");
            final String name = item.getString("name", "");
            final String code = item.getString("code", "");
            if (title.toLowerCase(Locale.ROOT).contains(normalized)
                || name.toLowerCase(Locale.ROOT).contains(normalized)
                || code.toLowerCase(Locale.ROOT).contains(normalized)) {
                filtered.add(item);
            }
        });
        return filtered;
    }

    private JsonArray paginatePurchased(final List<JsonObject> items, final JsonObject query) {
        final JsonObject pager = query == null ? null : query.getJsonObject("pager");
        final int page = pager == null ? 1 : Math.max(1, pager.getInteger("page", 1));
        final int size = pager == null ? 10 : Math.max(1, pager.getInteger("size", 10));
        final int start = (page - 1) * size;
        final JsonArray result = new JsonArray();
        if (start >= items.size()) {
            return result;
        }

        final int end = Math.min(items.size(), start + size);
        for (int index = start; index < end; index++) {
            result.add(items.get(index));
        }
        return result;
    }

    private String keyword(final JsonObject query) {
        if (query == null) {
            return null;
        }
        final JsonObject criteria = query.getJsonObject("criteria", new JsonObject());
        final String[] keys = {"name,c", "title,c", "keyword"};
        for (final String key : keys) {
            final String value = criteria.containsKey(key) ? criteria.getString(key) : query.getString(key);
            if (!Ut.isNil(value)) {
                return value;
            }
        }
        return null;
    }

    private XSubscription pickCurrent(final List<XSubscription> subscriptions) {
        return subscriptions.stream()
            .max(this::compareSubscriptionDesc)
            .orElse(null);
    }

    private int compareSubscriptionDesc(final XSubscription left, final XSubscription right) {
        return this.sortKey(right).compareTo(this.sortKey(left));
    }

    private LocalDateTime sortKey(final XSubscription subscription) {
        if (Objects.nonNull(subscription.getUpdatedAt())) {
            return subscription.getUpdatedAt();
        }
        if (Objects.nonNull(subscription.getCreatedAt())) {
            return subscription.getCreatedAt();
        }
        if (Objects.nonNull(subscription.getStartAt())) {
            return subscription.getStartAt();
        }
        return LocalDateTime.MIN;
    }

    private XSubscription createSubscription(
        final String tenantId,
        final String sigma,
        final XApp app,
        final JsonObject input,
        final String operatorId,
        final LocalDateTime now
    ) {
        final XSubscription subscription = new XSubscription();
        subscription.setId(UUID.randomUUID().toString());
        subscription.setTenantId(tenantId);
        subscription.setSigma(sigma);
        subscription.setAppId(app.getId());
        subscription.setPlanId(input.getString("planId"));
        subscription.setType("APP");
        subscription.setStatus("ACTIVE");
        subscription.setActive(Boolean.TRUE);
        subscription.setStartAt(this.parseDateTime(input.getString("startAt"), now));
        subscription.setEndAt(this.parseDateTime(input.getString("endAt"), null));
        subscription.setRenewType(input.getString("renewType"));
        subscription.setAutoRenew(Boolean.FALSE);
        subscription.setLanguage(this.resolveLanguage(input, app));
        subscription.setVersion(this.resolveVersion(input, app));
        subscription.setMetadata(this.purchaseMetadata(null, app, input, operatorId, now, true));
        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);
        subscription.setCreatedBy(operatorId);
        subscription.setUpdatedBy(operatorId);
        return subscription;
    }

    private XSubscription activateSubscription(
        final XSubscription subscription,
        final String tenantId,
        final String sigma,
        final XApp app,
        final JsonObject input,
        final String operatorId,
        final LocalDateTime now
    ) {
        subscription.setTenantId(tenantId);
        subscription.setSigma(sigma);
        subscription.setAppId(app.getId());
        if (Ut.isNil(subscription.getPlanId())) {
            subscription.setPlanId(input.getString("planId"));
        }
        subscription.setType("APP");
        subscription.setStatus("ACTIVE");
        subscription.setActive(Boolean.TRUE);
        if (Objects.isNull(subscription.getStartAt())) {
            subscription.setStartAt(now);
        }
        final LocalDateTime endAt = this.parseDateTime(input.getString("endAt"), subscription.getEndAt());
        subscription.setEndAt(endAt);
        if (Ut.isNil(subscription.getRenewType())) {
            subscription.setRenewType(input.getString("renewType"));
        }
        if (Objects.isNull(subscription.getAutoRenew())) {
            subscription.setAutoRenew(Boolean.FALSE);
        }
        subscription.setLanguage(this.resolveLanguage(input, app));
        subscription.setVersion(this.resolveVersion(input, app));
        subscription.setMetadata(this.purchaseMetadata(
            subscription.getMetadata(), app, input, operatorId, now, false
        ));
        subscription.setUpdatedAt(now);
        subscription.setUpdatedBy(operatorId);
        return subscription;
    }

    private JsonObject purchaseMetadata(
        final JsonObject existing,
        final XApp app,
        final JsonObject input,
        final String operatorId,
        final LocalDateTime now,
        final boolean created
    ) {
        final JsonObject metadata = Objects.isNull(existing) ? new JsonObject() : existing.copy();
        final JsonObject purchase = metadata.getJsonObject("purchase", new JsonObject());
        purchase.put("source", input.getString("source", "app-store"));
        purchase.put("entry", input.getString("entry", "store"));
        purchase.put("appId", app.getId());
        purchase.put("appName", app.getName());
        purchase.put("title", app.getTitle());
        purchase.put("version", this.resolveVersion(input, app));
        purchase.put(created ? "createdAt" : "reActivatedAt", now.toString());
        if (!Ut.isNil(operatorId)) {
            purchase.put("operatorId", operatorId);
        }
        if (!Ut.isNil(input.getString("operatorName"))) {
            purchase.put("operatorName", input.getString("operatorName"));
        }
        if (!Ut.isNil(input.getString("orderNo"))) {
            purchase.put("orderNo", input.getString("orderNo"));
        }
        metadata.put("purchase", purchase);

        final JsonArray purchaseLog = metadata.getJsonArray("purchaseLog", new JsonArray());
        purchaseLog.add(new JsonObject()
            .put("action", created ? "PURCHASE" : "REACTIVATE")
            .put("timestamp", now.toString())
            .put("source", input.getString("source", "app-store"))
            .put("entry", input.getString("entry", "store"))
            .put("operatorId", operatorId)
            .put("appId", app.getId()));
        metadata.put("purchaseLog", purchaseLog);
        return metadata;
    }

    private String resolveLanguage(final JsonObject input, final XApp app) {
        final String language = input.getString("language");
        if (!Ut.isNil(language)) {
            return language;
        }
        if (Objects.nonNull(app) && !Ut.isNil(app.getLanguage())) {
            return app.getLanguage();
        }
        return "zh-CN";
    }

    private String resolveVersion(final JsonObject input, final XApp app) {
        final String version = input.getString("version");
        if (!Ut.isNil(version)) {
            return version;
        }
        return Objects.isNull(app) ? null : app.getVersion();
    }

    private LocalDateTime parseDateTime(final String value, final LocalDateTime defaultValue) {
        if (Ut.isNil(value)) {
            return defaultValue;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (final RuntimeException ignored) {
            return defaultValue;
        }
    }

    private JsonObject toPurchasedItem(final XSubscription subscription, final XApp app) {
        final JsonObject item = new JsonObject();
        item.put("id", subscription.getId());
        item.put("appId", subscription.getAppId());
        item.put("purchaseAt", this.dateTimeString(subscription.getCreatedAt(), subscription.getStartAt()));
        item.put("expireAt", this.dateTimeString(subscription.getEndAt(), null));
        item.put("status", this.displayStatus(subscription));
        item.put("active", this.isActive(subscription));
        if (Objects.nonNull(app)) {
            item.put("name", app.getName());
            item.put("title", Ut.isNil(app.getTitle()) ? app.getName() : app.getTitle());
            item.put("icon", app.getLogo());
            item.put("code", app.getCode());
            item.put("version", Ut.isNil(subscription.getVersion()) ? app.getVersion() : subscription.getVersion());
        } else {
            item.put("name", null);
            item.put("title", null);
            item.put("icon", null);
            item.put("code", null);
            item.put("version", subscription.getVersion());
        }
        return item;
    }

    private String displayStatus(final XSubscription subscription) {
        String displayStatus = subscription.getStatus();
        if ("ACTIVE".equals(subscription.getStatus()) && subscription.getEndAt() != null) {
            final LocalDateTime now = LocalDateTime.now();
            if (subscription.getEndAt().isBefore(now)) {
                displayStatus = "EXPIRED";
            } else if (subscription.getEndAt().isBefore(now.plusDays(7))) {
                displayStatus = "EXPIRING_SOON";
            }
        }
        return displayStatus;
    }

    private boolean isActive(final XSubscription subscription) {
        return Boolean.TRUE.equals(subscription.getActive()) && !"EXPIRED".equals(this.displayStatus(subscription));
    }

    private String dateTimeString(final LocalDateTime value, final LocalDateTime fallback) {
        final LocalDateTime target = Objects.nonNull(value) ? value : fallback;
        return Objects.isNull(target) ? null : target.toString();
    }

    private JsonObject emptyPurchased() {
        return new JsonObject().put("list", new JsonArray()).put("count", 0);
    }

    private JsonObject error(final String message) {
        return new JsonObject().put("error", message);
    }
}
