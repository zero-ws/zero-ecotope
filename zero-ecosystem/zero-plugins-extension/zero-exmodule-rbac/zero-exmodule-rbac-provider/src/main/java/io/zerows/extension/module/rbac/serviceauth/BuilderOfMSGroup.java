package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.element.MSGroup;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.domain.tables.pojos.SGroup;
import io.zerows.support.Ut;

import java.util.Optional;
import java.util.UUID;

public class BuilderOfMSGroup extends AbstractBuilder<MSGroup> {
    @Override
    @SuppressWarnings("all")
    public <R> MSGroup create(final R source) {
        if (source instanceof final SGroup groupEntity) {
            final MSGroup entity = new MSGroup();
            entity.setName(groupEntity.getName());
            entity.setCode(groupEntity.getCode());

            // Parent Id + Type
            Optional.ofNullable(groupEntity.getParentId())
                .map(UUID::fromString)
                .ifPresent(entity::setParentId);
            entity.setType(groupEntity.getCategory());


            // metadata
            entity.extension(KName.METADATA, Ut.toJObject(groupEntity.getMetadata()));

            // Active
            R2MO.vActive(entity,
                groupEntity::getActive,
                groupEntity::getVersion,
                groupEntity::getLanguage
            );

            // Scope
            R2MO.vScope(entity,
                groupEntity::getAppId,
                groupEntity::getTenantId,
                groupEntity::getId
            );

            // Audit
            R2MO.vAudit(entity,
                groupEntity::getCreatedBy,
                groupEntity::getCreatedAt,
                groupEntity::getUpdatedBy,
                groupEntity::getUpdatedAt
            );
            return entity;
        }
        return null;
    }
}
