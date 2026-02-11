package io.zerows.extension.module.rbac.metadata.logged;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.boot.MDRBACManager;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/*
 * Single authority for role -> permissions
 * 1) priority
 * 2) permissions
 */
@Data
public class ProfileRole implements Serializable {

    private static final ScConfig CONFIG = MDRBACManager.of().config();
    @Setter(AccessLevel.NONE)
    private final Integer priority;
    private final ScRole role;
    /* GroupId Process */
    @Accessors(chain = true)
    private ProfileGroup group;

    public ProfileRole(final JsonObject data) {
        /* Role Id */
        final String roleId = data.getString(ScAuthKey.F_ROLE_ID);
        this.role = ScRole.login(roleId);
        /* Priority */
        this.priority = data.getInteger(ScAuthKey.PRIORITY);
    }

    Future<ProfileRole> initAsync() {
        /* Fetch permission */
        final boolean isSecondary = CONFIG.getSupportSecondary();
        return isSecondary ?
            /* Enabled secondary permission */
            this.role.fetchWithCache().compose(ids -> Future.succeededFuture(this)) :
            /* No secondary */
            this.role.fetch().compose(ids -> Future.succeededFuture(this));
    }

    public ProfileRole init() {
        /* Fetch permission ( Without Cache in Sync mode ) */
        this.role.refresh();
        // Sc.infoAuth(LOGGER, "Extract Permissions: {0}", permissions.encode());
        return this;
    }

    public String getKey() {
        return this.role.key();
    }

    public Set<String> getAuthorities() {
        return this.role.authorities();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ProfileRole that = (ProfileRole) o;
        return this.role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.role);
    }

    @Override
    public String toString() {
        return "ProfileRole{" +
            "priority=" + this.priority +
            ", role=" + this.role +
            ", group=" + this.group +
            '}';
    }
}
