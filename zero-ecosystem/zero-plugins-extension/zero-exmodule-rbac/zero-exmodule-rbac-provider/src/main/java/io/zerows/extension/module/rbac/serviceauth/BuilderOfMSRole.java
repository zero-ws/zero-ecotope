package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.element.MSRole;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;

import java.util.Objects;

public class BuilderOfMSRole extends AbstractBuilder<MSRole> {

    @Override
    @SuppressWarnings("all")
    public <R> MSRole create(final R source) {
        if (source instanceof final SRole roleEntity) {
            final MSRole entity = new MSRole();
            // 检查 NullPointerException
            final Boolean isAdmin = Objects.isNull(roleEntity.getPower()) ? Boolean.FALSE : roleEntity.getPower();
            entity.setAdmin(isAdmin);
            entity.setName(roleEntity.getName());
            entity.setCode(roleEntity.getCode());

            // metadata
            entity.extension(KName.METADATA, roleEntity.getMetadata());

            // Active
            R2MO.vActive(entity,
                roleEntity::getActive,
                roleEntity::getVersion,
                roleEntity::getLanguage
            );

            // Scope
            R2MO.vScope(entity,
                roleEntity::getAppId,
                roleEntity::getTenantId,
                roleEntity::getId
            );

            // Audit
            R2MO.vAudit(entity,
                roleEntity::getCreatedBy,
                roleEntity::getCreatedAt,
                roleEntity::getUpdatedBy,
                roleEntity::getUpdatedAt
            );
            return entity;
        }
        return null;
    }
}
