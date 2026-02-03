package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.element.MSRole;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.support.Ut;

public class BuilderOfMSRole extends AbstractBuilder<MSRole> {

    @Override
    @SuppressWarnings("all")
    public <R> MSRole create(final R source) {
        if (source instanceof final SRole roleEntity) {
            final MSRole entity = new MSRole();
            entity.setAdmin(roleEntity.getPower());
            entity.setName(roleEntity.getName());
            entity.setCode(roleEntity.getCode());

            // metadata
            entity.extension(KName.METADATA, Ut.toJObject(roleEntity.getMetadata()));

            // Active
            R2MO.vActive(entity,
                roleEntity::getActive,
                roleEntity::getVersion,
                roleEntity::getLanguage
            );

            // Scope
            R2MO.vScope(entity,
                roleEntity::getId,
                roleEntity::getAppId,
                roleEntity::getTenantId
            );

            // Audit
            R2MO.vAudit(entity,
                roleEntity::getCreatedBy,
                roleEntity::getCreatedAt,
                roleEntity::getUpdatedBy,
                roleEntity::getUpdatedAt
            );
        }
        return null;
    }
}
