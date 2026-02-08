package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.typed.domain.extension.AbstractBuilder;
import io.r2mo.typed.enums.TypeID;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;

import java.util.Optional;

public class BuilderOfMSUser extends AbstractBuilder<MSUser> {
    @Override
    public <R> MSUser create(final R source) {
        if (source instanceof final SUser user) {
            final MSUser entity = new MSUser();
            entity.setUsername(user.getUsername());
            entity.setNickname(user.getAlias());
            entity.setPassword(user.getPassword());
            entity.setAvator(user.getAvatar());
            entity.setEmail(user.getEmail());
            entity.setMobile(user.getMobile());
            entity.setDescription(user.getDescription());

            {
                /*
                 * - code
                 * - type
                 * - category
                 * - realname
                 */
                entity.extension(KName.CODE, user.getCode());
                entity.extension(KName.TYPE, user.getType());
                entity.extension(KName.CATEGORY, user.getCategory());
                entity.extension(KName.REAL_NAME, user.getRealname());
            }
            {
                // alipay
                Optional.ofNullable(user.getAlipay()).map(new LoginID()::id)
                    .ifPresent(alipay -> entity.id(TypeID.ALIPAY, alipay));


                // ldapId / ldapMail,  directoryId, ldapPath
                Optional.ofNullable(user.getLdapId()).map(ldapId -> {
                    final LoginID ldapOf = new LoginID();
                    ldapOf.id(ldapId);
                    ldapOf.email(user.getLdapMail());
                    ldapOf.attribute("ldapPath", user.getLdapPath());
                    ldapOf.attribute("directoryId", user.getDirectoryId());
                    return ldapOf;
                }).ifPresent(ldap -> entity.id(TypeID.LDAP, ldap));


                // weId, weOpen, weUnion
                Optional.ofNullable(user.getWeId()).map(weId -> {
                    final LoginID weChat = new LoginID();
                    weChat.id(weId);
                    weChat.attribute("weOpen", user.getWeOpen());
                    weChat.attribute("weUnion", user.getWeUnion());
                    return weChat;
                }).ifPresent(weChat -> entity.id(TypeID.WECHAT, weChat));


                // cpId, cpOpen, cpUnion
                Optional.ofNullable(user.getCpId()).map(cpId -> {
                    final LoginID corpWe = new LoginID();
                    corpWe.id(cpId);
                    corpWe.attribute("cpOpen", user.getCpOpen());
                    corpWe.attribute("cpUnion", user.getCpUnion());
                    return corpWe;
                }).ifPresent(corpWe -> entity.id(TypeID.WECOM, corpWe));
            }

            R2MO.vModel(entity,
                user::getModelId,
                user::getModelKey
            );

            // metadata
            entity.extension(KName.METADATA, user.getMetadata());

            // Active
            R2MO.vActive(entity,
                user::getActive,
                null,
                user::getLanguage
            );

            // Scope
            R2MO.vScope(entity,
                user::getId,
                user::getAppId,
                user::getTenantId
            );

            // Audit
            R2MO.vAudit(entity,
                user::getCreatedBy,
                user::getCreatedAt,
                user::getUpdatedBy,
                user::getUpdatedAt
            );

            return entity;
        }
        return null;
    }
}
