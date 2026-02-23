package io.zerows.extension.module.rbac.serviceauth;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.element.MSGroup;
import io.r2mo.jaas.element.MSRole;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.extension.module.rbac.servicespec.UserLinkStub;
import io.zerows.platform.metadata.KRef;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserAuthService implements UserAuthStub {
    private final ADB dbUser = DB.on(SUserDao.class);
    @Inject
    private UserLinkStub linkStub;

    @Override
    public Future<MSUser> whereUsername(final String username) {
        return this.dbUser.<SUser>fetchOneAsync(KName.USERNAME, username)
            .compose(this::fetchComplex);
    }

    private Future<MSUser> fetchComplex(final SUser user) {
        if (Objects.isNull(user)) {
            return Future.succeededFuture();
        }
        final KRef listRef = new KRef();
        final String userId = user.getId();
        return Future.succeededFuture(userId)
            .compose(this.linkStub::rolesByUser).compose(listRef::future) // 获取角色列表
            .map(nil -> userId)
            .compose(this.linkStub::groupsByUser).compose(listRef::standBy) // 获取用户组列表
            .compose(groups -> this.fetchComplex(user, listRef.get(), listRef.getStandBy()));
    }

    private Future<MSUser> fetchComplex(final SUser user, final List<MSRole> roles, final List<MSGroup> groups) {
        final BuilderOf<MSUser> builder = BuilderOf.of(BuilderOfMSUser::new);
        final MSUser msUser = builder.create(user);
        // 角色是平行角色
        Optional.ofNullable(roles).ifPresent(msUser::roles);
        // 组加载
        Optional.ofNullable(groups).ifPresent(msUser::groups);
        return Future.succeededFuture(msUser);
    }

    @Override
    public Future<MSUser> whereBy(final String id, final TypeLogin typeID) {
        final String field = this.whereField(typeID);
        if (StrUtil.isEmpty(field)) {
            return Future.succeededFuture();
        }
        return this.dbUser.<SUser>fetchOneAsync(field, id)
            .compose(this::fetchComplex);
    }

    private String whereField(final TypeLogin typeId) {
        return switch (typeId) {
            case LDAP -> "ldapEmail";
            case SMS -> "mobile";
            case EMAIL -> "email";
            // Fix: [ R2MO ] 无法找到对应的 Column 名称: 输入字段 = weUnionId, 绑定实体 = null, messageDisplay=null)
            case ID_WECHAT -> "weUnion";
            case ID_WECOM -> "cpUnion";
            default -> null;
        };
    }
}
