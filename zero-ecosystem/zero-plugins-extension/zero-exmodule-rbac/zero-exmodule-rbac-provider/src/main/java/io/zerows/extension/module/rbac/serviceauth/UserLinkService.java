package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.jaas.element.MSGroup;
import io.r2mo.jaas.element.MSRole;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.vertx.core.Future;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.RGroupRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SGroupDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RGroupRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserGroup;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SGroup;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.module.rbac.servicespec.UserLinkStub;
import io.zerows.support.Fx;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserLinkService implements UserLinkStub {

    @Override
    public Future<List<MSGroup>> groupsByUser(final String userId) {
        return DB.on(RUserGroupDao.class).<RUserGroup>fetchAsync(KName.USER_ID, userId).compose(relations -> {
            final Set<String> groupIds = Ut.elementSet(relations, RUserGroup::getGroupId);
            return DB.on(SGroupDao.class).<SGroup>fetchInAsync(KName.ID, groupIds);
        }).compose(groups -> {
            final BuilderOf<MSGroup> builder = BuilderOf.of(BuilderOfMSGroup::new);
            final List<Future<MSGroup>> waitFor = new ArrayList<>();
            groups.forEach(group -> {
                final MSGroup msGroup = builder.create(group);
                waitFor.add(this.rolesByGroup(msGroup));
            });
            return Fx.combineT(waitFor);
        });
    }

    private Future<MSGroup> rolesByGroup(final MSGroup group) {
        if (Objects.isNull(group.getId())) {
            return Future.succeededFuture(group);
        }
        return this.rolesByGroup(group.getId().toString()).compose(roles -> {
            group.roles(roles);
            return Future.succeededFuture(group);
        });
    }

    @Override
    public Future<List<MSRole>> rolesByGroup(final String groupId) {
        return DB.on(RGroupRoleDao.class).<RGroupRole>fetchAsync(KName.GROUP_ID, groupId).compose(relations -> {
            final Set<String> roleIds = Ut.elementSet(relations, RGroupRole::getRoleId);
            // 组合角色信息和关系信息
            return DB.on(SRoleDao.class).<SRole>fetchInAsync(KName.ID, roleIds).map(roles -> {
                final List<MSRole> roleList = new ArrayList<>();
                final BuilderOf<MSRole> builder = BuilderOf.of(BuilderOfMSRole::new);
                roles.stream().filter(SRole::getActive).forEach(role -> {
                    final MSRole msRole = builder.create(role);
                    final RGroupRole found = relations.stream()
                        .filter(relation -> Objects.equals(relation.getRoleId(), role.getId()))
                        .findAny().orElse(null);
                    Objects.requireNonNull(found);      // 不可能为空，因为此处是通过关系查询的
                    msRole.setPriority(found.getPriority());
                    roleList.add(msRole);
                });
                return roleList;
            });
        });
    }

    @Override
    public Future<List<MSRole>> rolesByUser(final String userId) {
        return DB.on(RUserRoleDao.class).<RUserRole>fetchAsync(KName.USER_ID, userId).compose(relations -> {
            final Set<String> roleIds = Ut.elementSet(relations, RUserRole::getRoleId);
            // 组合角色信息和关系信息
            return DB.on(SRoleDao.class).<SRole, String>fetchInAsync(KName.ID, roleIds).map(roles -> {
                final List<MSRole> roleList = new ArrayList<>();
                final BuilderOf<MSRole> builder = BuilderOf.of(BuilderOfMSRole::new);
                roles.stream().filter(SRole::getActive).forEach(role -> {
                    final MSRole msRole = builder.create(role);
                    final RUserRole found = relations.stream()
                        .filter(relation -> Objects.equals(relation.getRoleId(), role.getId()))
                        .findAny().orElse(null);
                    Objects.requireNonNull(found);      // 不可能为空，因为此处是通过关系查询的
                    msRole.setPriority(found.getPriority());
                    roleList.add(msRole);
                });
                return roleList;
            });
        });
    }
}
