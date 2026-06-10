package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.common.em.InstanceStatus;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppInstanceDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XAppInstance;
import io.zerows.extension.module.ambient.exception._80306Exception404InstanceNotFound;
import io.zerows.extension.module.ambient.exception._80307Exception400InstanceBadRequest;
import io.zerows.extension.module.ambient.servicespec.InstanceStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Locale;

public class InstanceService implements InstanceStub {

    @Override
    public Future<JsonArray> search(final JsonObject criteria) {
        final JsonObject qr = Ux.whereAnd();
        if (criteria.containsKey(KName.SIGMA)) {
            qr.put(KName.SIGMA, criteria.getString(KName.SIGMA));
        }
        if (criteria.containsKey(KName.APP_ID)) {
            qr.put(KName.APP_ID, criteria.getString(KName.APP_ID));
        }
        if (criteria.containsKey("tenantId")) {
            qr.put("tenantId", criteria.getString("tenantId"));
        }
        if (criteria.containsKey(KName.STATUS)) {
            qr.put(KName.STATUS, criteria.getString(KName.STATUS));
        }
        if (criteria.containsKey("healthStatus")) {
            qr.put("healthStatus", criteria.getString("healthStatus"));
        }
        if (criteria.containsKey("deployMode")) {
            qr.put("deployMode", criteria.getString("deployMode"));
        }
        final String keyword = criteria.getString("keyword");
        return DB.on(XAppInstanceDao.class).<XAppInstance>fetchAsync(qr)
            .compose(list -> {
                if (Ut.isNil(keyword)) {
                    return Ux.futureA(list);
                }
                final String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);
                for (final java.util.Iterator<XAppInstance> it = list.iterator(); it.hasNext(); ) {
                    final XAppInstance item = it.next();
                    final String name = item.getInstanceName();
                    if (null == name || !name.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                        it.remove();
                    }
                }
                return Ux.futureA(list);
            });
    }

    @Override
    public Future<JsonObject> getById(final String key) {
        return DB.on(XAppInstanceDao.class)
            .<XAppInstance>fetchByIdAsync(key)
            .compose(found -> {
                if (null == found) {
                    return Future.failedFuture(new _80306Exception404InstanceNotFound(key));
                }
                return Ux.futureJ(found);
            });
    }

    @Override
    public Future<JsonObject> upsert(final JsonObject instanceData) {
        final JsonObject normalized = this.normalizeInstanceData(instanceData);
        final String appId = instanceData.getString(KName.APP_ID);
        final String sigma = instanceData.getString(KName.SIGMA);
        if (Ut.isNil(appId) || Ut.isNil(sigma)) {
            return Future.failedFuture(new _80307Exception400InstanceBadRequest("appId and sigma are required"));
        }
        final JsonObject qr = Ux.whereAnd();
        qr.put(KName.APP_ID, appId);
        qr.put(KName.SIGMA, sigma);
        return DB.on(XAppInstanceDao.class).<XAppInstance>fetchOneAsync(qr)
            .compose(existing -> {
                if (null == existing) {
                    return DB.on(XAppInstanceDao.class)
                        .insertAsync(new XAppInstance(normalized))
                        .compose(Ux::futureJ);
                }
                normalized.put(KName.KEY, existing.getId());
                if (Ut.isNil(normalized.getString("TENANT_ID")) && Ut.isNotNil(existing.getTenantId())) {
                    normalized.put("TENANT_ID", existing.getTenantId());
                }
                return DB.on(XAppInstanceDao.class)
                    .updateJAsync(existing.getId(), normalized);
            });
    }

    @Override
    public Future<JsonObject> updateStatus(final String key, final String status) {
        if (null == InstanceStatus.from(status)) {
            return Future.failedFuture(new _80307Exception400InstanceBadRequest("Invalid instance status: " + status));
        }
        final JsonObject update = new JsonObject();
        update.put(KName.STATUS, InstanceStatus.from(status).value());
        return DB.on(XAppInstanceDao.class).updateJAsync(key, update);
    }

    @Override
    public Future<JsonObject> delete(final String key) {
        final JsonObject update = new JsonObject();
        update.put(KName.ACTIVE, Boolean.FALSE);
        return DB.on(XAppInstanceDao.class).updateJAsync(key, update);
    }

    private JsonObject normalizeInstanceData(final JsonObject source) {
        final JsonObject normalized = source.copy();
        this.copy(normalized, "appId", "APP_ID");
        this.copy(normalized, "sigma", "SIGMA");
        this.copy(normalized, "instanceName", "INSTANCE_NAME");
        this.copy(normalized, "status", "STATUS");
        this.copy(normalized, "dockerImage", "DOCKER_IMAGE");
        this.copy(normalized, "dockerContainer", "DOCKER_CONTAINER");
        this.copy(normalized, "databaseInstance", "DATABASE_INSTANCE");
        this.copy(normalized, "runtimeRoot", "RUNTIME_ROOT");
        this.copy(normalized, "instanceUrl", "INSTANCE_URL");
        this.copy(normalized, "version", "VERSION");
        this.copy(normalized, "compositeApp", "COMPOSITE_APP");
        this.copy(normalized, "installRoot", "INSTALL_ROOT");
        this.copy(normalized, "releaseId", "RELEASE_ID");
        this.copy(normalized, "packageId", "PACKAGE_ID");
        this.copy(normalized, "packageChecksum", "PACKAGE_CHECKSUM");
        this.copy(normalized, "deployMode", "DEPLOY_MODE");
        this.copy(normalized, "configHash", "CONFIG_HASH");
        this.copy(normalized, "domain", "DOMAIN");
        this.copy(normalized, "port", "PORT");
        this.copy(normalized, "context", "CONTEXT");
        this.copy(normalized, "tenantId", "TENANT_ID");
        this.copy(normalized, "healthStatus", "HEALTH_STATUS");
        this.copy(normalized, "healthAt", "HEALTH_AT");
        this.copy(normalized, "deployedAt", "DEPLOYED_AT");
        this.copy(normalized, "startedAt", "STARTED_AT");
        this.copy(normalized, "stoppedAt", "STOPPED_AT");
        this.copy(normalized, "active", "ACTIVE");
        this.copy(normalized, "language", "LANGUAGE");
        return normalized;
    }

    private void copy(final JsonObject source, final String from, final String to) {
        if (source.containsKey(from) && !source.containsKey(to)) {
            source.put(to, source.getValue(from));
        }
    }
}
