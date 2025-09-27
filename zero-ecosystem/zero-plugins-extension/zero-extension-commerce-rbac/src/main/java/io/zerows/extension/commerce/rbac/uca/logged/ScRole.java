package io.zerows.extension.commerce.rbac.uca.logged;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.cache.Rapid;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.module.metadata.uca.environment.DevEnv;
import io.zerows.unity.Ux;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * Data in Global Shared Data for current role
 * Connect to Pool: permissionPool
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ScRole {
    private static final Cc<String, ScRole> CC_ROLE = Cc.open();
    private static final Annal LOGGER = Annal.get(ScRole.class);
    private final transient Rapid<String, JsonArray> cache;
    private final transient String roleId;
    private final transient Set<String> authorities = new HashSet<>();

    private ScRole(final String roleId) {
        this.roleId = roleId;
        this.cache = Rapid.object(ScConstant.POOL_PERMISSIONS);
    }

    public static ScRole login(final String roleId) {
        return CC_ROLE.pick(() -> new ScRole(roleId), roleId);
        // return RFn.po?l(ROLES, roleId, () -> new ScRole(roleId));
    }

    public String key() {
        return this.roleId;
    }

    public Set<String> authorities() {
        return this.authorities;
    }

    // ------------------------- Initialized Method ------------------------
    public Future<JsonArray> clear() {
        CC_ROLE.get().remove(this.roleId);
        // ROLES.remove(this.roleId);
        return this.cache.clear(this.roleId);
    }

    /*
     * Secondary cache enabled here, fetch authorities
     * 1) Fetch data from cache with roleId = this.roleId
     * 2.1) If null: Fetch authorities from database
     * 2.2) If not null: Return authorities directly ( pick up from cache )
     */
    public Future<JsonArray> fetchWithCache() {
        /*
         * This workflow will action when each user login
         * It means that the cache pool could be refreshed when role
         * permissions
         */
        return this.permission().compose(permissions -> {
            if (Objects.isNull(permissions)) {
                return this.fetch().compose(this::permission);
            } else {
                if (DevEnv.devAuthorized()) {
                    LOG.Auth.info(LOGGER, "ScRole \u001b[0;37m----> Cache key = {0}\u001b[m.", this.roleId);
                }
                /* Authorities fill from cache ( Sync the authorities ) */
                permissions.stream().map(item -> (String) item)
                    .forEach(this.authorities::add);
                return Ux.future(permissions);
            }
        });
    }

    /*
     * Single authorities fetching
     * 1) Fetch data from database with roleId = this.roleId
     * 2) Extract data to JsonArray ( permission Ids )
     */
    public Future<JsonArray> fetch() {
        return Ux.Jooq.on(RRolePermDao.class)
            /* Fetch permission ids based on roleId */
            .<RRolePerm>fetchAsync(AuthKey.F_ROLE_ID, this.roleId)
            /*
             * Extract the latest relations: role - permissions
             * 1) Clear current profile authorities
             * 2) Refresh current profile authorities by input permissions
             * 3) Returned ( JsonArray )
             */
            .compose(permissions -> Ux.future(this.authorities(permissions)));
    }

    public void refresh() {
        /* Fetch permission ( Without Cache in Sync mode ) */
        final List<RRolePerm> queried = Ux.Jooq.on(RRolePermDao.class)
            .fetch(AuthKey.F_ROLE_ID, this.roleId);
        this.authorities(queried);
    }

    public Future<JsonArray> refresh(final JsonArray permissions) {
        return this.permission(permissions);
    }

    // ------------------------- Permission Method ------------------------
    private JsonArray authorities(final List<RRolePerm> permissions) {
        final List<String> permissionIds = permissions.stream()
            .filter(Objects::nonNull)
            .map(RRolePerm::getPermId)
            .collect(Collectors.toList());
        this.authorities.clear();
        this.authorities.addAll(permissionIds);
        return Ut.toJArray(permissionIds);
    }

    /*
     * Pool configured default parameters
     * - permissionPool
     * This pool is for permission of role:
     * key = role id
     * - S_ROLE ( key )
     * value = permissions ( JsonArray )
     */
    private Future<JsonArray> permission() {
        return this.cache.read(this.roleId);
    }

    private Future<JsonArray> permission(final JsonArray permission) {
        return this.cache.write(this.roleId, permission);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ScRole scRole = (ScRole) o;
        return this.roleId.equals(scRole.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.roleId);
    }

    @Override
    public String toString() {
        return "ScRole{" +
            "roleId='" + this.roleId + '\'' +
            ", authorities=" + this.authorities +
            '}';
    }
}
