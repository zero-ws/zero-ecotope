package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XTenantDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.extension.module.ambient.servicespec.TenantStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;

/**
 * Tenant Management Service Implementation
 *
 * @author lang : 2026-02-14
 */
public class TenantService implements TenantStub {

    @Override
    public Future<JsonObject> activate(final String tenantKey, final String reason) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.future(new JsonObject().put("error", "Tenant not found"));
            }

            final String currentStatus = tenant.getStatus();

            // Update status and active flag
            tenant.setStatus("ACTIVE");
            tenant.setActive(true);
            tenant.setUpdatedAt(LocalDateTime.now());

            // Record reason in metadata if provided
            if (!Ut.isNil(reason)) {
                final JsonObject metadata = tenant.getMetadata() == null ? new JsonObject() : tenant.getMetadata();
                final JsonArray statusLog = metadata.getJsonArray("statusLog", new JsonArray());
                statusLog.add(new JsonObject()
                    .put("action", "ACTIVATE")
                    .put("reason", reason)
                    .put("timestamp", LocalDateTime.now().toString())
                    .put("fromStatus", currentStatus)
                    .put("toStatus", "ACTIVE"));
                metadata.put("statusLog", statusLog);
                tenant.setMetadata(metadata);
            }

            return DB.on(XTenantDao.class).updateAsync(tenant).compose(Ux::futureJ);
        });
    }

    @Override
    public Future<JsonObject> deactivate(final String tenantKey, final String reason) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.future(new JsonObject().put("error", "Tenant not found"));
            }

            final String currentStatus = tenant.getStatus();

            // Update status and active flag
            tenant.setStatus("FROZEN");
            tenant.setActive(false);
            tenant.setUpdatedAt(LocalDateTime.now());

            // Record reason in metadata (required)
            final JsonObject metadata = tenant.getMetadata() == null ? new JsonObject() : tenant.getMetadata();
            final JsonArray statusLog = metadata.getJsonArray("statusLog", new JsonArray());
            statusLog.add(new JsonObject()
                .put("action", "DEACTIVATE")
                .put("reason", reason)
                .put("timestamp", LocalDateTime.now().toString())
                .put("fromStatus", currentStatus)
                .put("toStatus", "FROZEN"));
            metadata.put("statusLog", statusLog);
            tenant.setMetadata(metadata);

            return DB.on(XTenantDao.class).updateAsync(tenant).compose(Ux::futureJ);
        });
    }

    @Override
    public Future<JsonArray> fetchStatusHistory(final String tenantKey, final Integer page, final Integer size) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.futureA();
            }

            final JsonObject metadata = tenant.getMetadata();
            if (metadata == null || !metadata.containsKey("statusLog")) {
                return Ux.futureA();
            }

            final JsonArray statusLog = metadata.getJsonArray("statusLog", new JsonArray());

            // Pagination
            final int start = (page - 1) * size;
            final int end = Math.min(start + size, statusLog.size());

            if (start >= statusLog.size()) {
                return Ux.futureA();
            }

            final JsonArray result = new JsonArray();
            for (int i = start; i < end; i++) {
                result.add(statusLog.getValue(i));
            }

            return Ux.future(result);
        });
    }

    @Override
    public Future<JsonObject> fetchQuota(final String tenantKey) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.future(new JsonObject().put("error", "Tenant not found"));
            }

            final JsonObject metadata = tenant.getMetadata();
            if (metadata == null) {
                return Ux.future(this.getDefaultQuota());
            }

            final JsonObject quota = metadata.getJsonObject("quota", this.getDefaultQuota().getJsonObject("quota"));
            final JsonObject usage = metadata.getJsonObject("usage", new JsonObject());

            // Calculate usage rates
            final JsonObject result = new JsonObject();
            result.put("quota", quota);
            result.put("usage", usage);
            result.put("usageRate", this.calculateUsageRate(quota, usage));

            return Ux.future(result);
        });
    }

    @Override
    public Future<JsonObject> updateQuota(final String tenantKey, final JsonObject quotaData, final String reason) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.future(new JsonObject().put("error", "Tenant not found"));
            }

            final JsonObject metadata = tenant.getMetadata() == null ? new JsonObject() : tenant.getMetadata();
            final JsonObject currentUsage = metadata.getJsonObject("usage", new JsonObject());

            // Validate: new quota must be >= current usage
            if (!this.validateQuota(quotaData, currentUsage)) {
                return Ux.future(new JsonObject().put("error", "New quota cannot be lower than current usage"));
            }

            // Update quota
            metadata.put("quota", quotaData);

            // Record quota change history
            final JsonArray quotaHistory = metadata.getJsonArray("quotaHistory", new JsonArray());
            quotaHistory.add(new JsonObject()
                .put("quota", quotaData)
                .put("reason", reason)
                .put("timestamp", LocalDateTime.now().toString()));
            metadata.put("quotaHistory", quotaHistory);

            tenant.setMetadata(metadata);
            tenant.setUpdatedAt(LocalDateTime.now());

            return DB.on(XTenantDao.class).updateAsync(tenant)
                .compose(updated -> this.fetchQuota(tenantKey));
        });
    }

    @Override
    public Future<JsonObject> fetchStatistics(final String tenantKey) {
        return this.fetchTenant(tenantKey).compose(tenant -> {
            if (tenant == null) {
                return Ux.future(new JsonObject().put("error", "Tenant not found"));
            }

            final JsonObject metadata = tenant.getMetadata();
            final JsonObject usage = metadata != null ? metadata.getJsonObject("usage", new JsonObject()) : new JsonObject();

            final JsonObject statistics = new JsonObject();
            statistics.put("tenantId", tenant.getId());
            statistics.put("tenantName", tenant.getName());
            statistics.put("status", tenant.getStatus());
            statistics.put("active", tenant.getActive());
            statistics.put("userCount", usage.getInteger("userCount", 0));
            statistics.put("appCount", usage.getInteger("appCount", 0));
            statistics.put("storageUsed", usage.getLong("storageUsed", 0L));
            statistics.put("bandwidthUsed", usage.getLong("bandwidthUsed", 0L));
            statistics.put("apiCallsToday", usage.getInteger("apiCallsToday", 0));
            statistics.put("lastActivityTime", usage.getString("lastActivityTime"));
            statistics.put("createdAt", tenant.getCreatedAt() != null ? tenant.getCreatedAt().toString() : null);

            return Ux.future(statistics);
        });
    }

    // Helper methods

    private Future<XTenant> fetchTenant(final String tenantKey) {
        // Try to fetch by ID first, then by code
        return DB.on(XTenantDao.class).<XTenant>fetchByIdAsync(tenantKey)
            .compose(tenant -> {
                if (tenant != null) {
                    return Ux.future(tenant);
                }
                // Try by code
                return DB.on(XTenantDao.class).<XTenant>fetchOneAsync(KName.CODE, tenantKey);
            });
    }

    private JsonObject getDefaultQuota() {
        final JsonObject quota = new JsonObject()
            .put("userLimit", 100)
            .put("storageLimit", 10737418240L) // 10GB
            .put("appLimit", 10)
            .put("apiCallLimit", 100000);
        return new JsonObject().put("quota", quota);
    }

    private JsonObject calculateUsageRate(final JsonObject quota, final JsonObject usage) {
        final JsonObject rate = new JsonObject();

        if (quota.containsKey("userLimit") && usage.containsKey("userCount")) {
            final int limit = quota.getInteger("userLimit", 1);
            final int used = usage.getInteger("userCount", 0);
            rate.put("userRate", limit > 0 ? (used * 100.0 / limit) : 0);
        }

        if (quota.containsKey("storageLimit") && usage.containsKey("storageUsed")) {
            final long limit = quota.getLong("storageLimit", 1L);
            final long used = usage.getLong("storageUsed", 0L);
            rate.put("storageRate", limit > 0 ? (used * 100.0 / limit) : 0);
        }

        if (quota.containsKey("appLimit") && usage.containsKey("appCount")) {
            final int limit = quota.getInteger("appLimit", 1);
            final int used = usage.getInteger("appCount", 0);
            rate.put("appRate", limit > 0 ? (used * 100.0 / limit) : 0);
        }

        if (quota.containsKey("apiCallLimit") && usage.containsKey("apiCallsToday")) {
            final int limit = quota.getInteger("apiCallLimit", 1);
            final int used = usage.getInteger("apiCallsToday", 0);
            rate.put("apiCallRate", limit > 0 ? (used * 100.0 / limit) : 0);
        }

        return rate;
    }

    private boolean validateQuota(final JsonObject newQuota, final JsonObject currentUsage) {
        // Check userLimit
        if (newQuota.containsKey("userLimit") && currentUsage.containsKey("userCount")) {
            if (newQuota.getInteger("userLimit") < currentUsage.getInteger("userCount")) {
                return false;
            }
        }

        // Check storageLimit
        if (newQuota.containsKey("storageLimit") && currentUsage.containsKey("storageUsed")) {
            if (newQuota.getLong("storageLimit") < currentUsage.getLong("storageUsed")) {
                return false;
            }
        }

        // Check appLimit
        if (newQuota.containsKey("appLimit") && currentUsage.containsKey("appCount")) {
            if (newQuota.getInteger("appLimit") < currentUsage.getInteger("appCount")) {
                return false;
            }
        }

        return true;
    }
}
