package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XTenantDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.extension.skeleton.spi.ExTenantProvision;
import io.zerows.program.Ux;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class ExTenantProvisionAmbient implements ExTenantProvision {

    @Override
    public Future<JsonObject> provision(final JsonObject input) {
        final String identifier = input.getString("identifier");
        final String type = input.getString("type", "UNKNOWN");
        final String normalized = this.normalize(identifier);
        final String tenantCode = "TENANT_" + normalized;
        return DB.on(XTenantDao.class).<XTenant>fetchOneAsync("code", tenantCode)
            .compose(found -> {
                if (Objects.nonNull(found)) {
                    return Ux.futureJ(found);
                }
                final String tenantId = UUID.randomUUID().toString();
                final XTenant tenant = new XTenant();
                tenant.setId(tenantId);
                tenant.setTenantId(tenantId);
                tenant.setCode(tenantCode);
                tenant.setName(identifier);
                tenant.setAlias(identifier);
                tenant.setContact(identifier);
                if ("EMAIL".equalsIgnoreCase(type)) {
                    tenant.setEmail(identifier);
                } else if ("SMS".equalsIgnoreCase(type)) {
                    tenant.setPhone(identifier);
                }
                tenant.setType("PERSONAL");
                tenant.setStatus("ACTIVE");
                tenant.setSigma("AUTO_" + normalized);
                tenant.setActive(Boolean.TRUE);
                tenant.setLanguage("zh-CN");
                tenant.setMetadata(new JsonObject()
                    .put("registerType", type)
                    .put("registerIdentifier", identifier));
                tenant.setCreatedAt(LocalDateTime.now());
                return DB.on(XTenantDao.class).insertAsync(tenant).compose(Ux::futureJ);
            });
    }

    private String normalize(final String identifier) {
        if (Objects.isNull(identifier) || identifier.isBlank()) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return identifier
            .toUpperCase(Locale.ROOT)
            .replaceAll("[^0-9A-Z]+", "_")
            .replaceAll("^_+|_+$", "");
    }
}
