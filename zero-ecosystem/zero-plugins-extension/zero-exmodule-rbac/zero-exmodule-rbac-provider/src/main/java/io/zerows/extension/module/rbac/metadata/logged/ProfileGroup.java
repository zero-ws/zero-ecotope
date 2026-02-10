package io.zerows.extension.module.rbac.metadata.logged;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.support.Fx;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * Single middle fetchProfile for group
 */
@Data
public class ProfileGroup implements Serializable {

    private transient final String groupId;
    @Getter
    private transient final Integer priority;
    private transient final JsonArray role;
    private transient final List<ProfileRole> roles = new ArrayList<>();
    @Accessors(chain = true)
    private transient String reference;

    public ProfileGroup(final JsonObject data) {
        /* Group Id */
        this.groupId = data.getString(ScAuthKey.F_GROUP_ID);
        /* Priority */
        this.priority = data.getInteger(ScAuthKey.PRIORITY);
        /* Role */
        this.role = null == data.getJsonArray("role")
            ? new JsonArray() : data.getJsonArray("role");
    }

    Future<ProfileGroup> initAsync() {
        /* No determine */
        return this.fetchProfilesAsync().compose(profiles -> {
            /* Clear and add */
            this.setRoles(profiles);
            return Future.succeededFuture(this);
        });
    }

    public ProfileGroup init() {
        this.setRoles(this.fetchProfiles());
        return this;
    }

    public String getKey() {
        return this.groupId;
    }

    private void setRoles(final List<ProfileRole> profiles) {
        this.roles.clear();
        this.roles.addAll(profiles);
    }

    /*
     * Extract the latest relations: initAsync role for each group fetchProfile
     */
    private Future<List<ProfileRole>> fetchProfilesAsync() {
        final List<Future<ProfileRole>> futures = new ArrayList<>();
        this.role.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .map(ProfileRole::new)
            .map(ProfileRole::initAsync)
            .forEach(futures::add);
        return Fx.combineT(futures).compose(profiles -> {
            profiles.forEach(profile -> profile.setGroup(this));
            return Future.succeededFuture(profiles);
        });
    }

    private List<ProfileRole> fetchProfiles() {
        return this.role.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .map(ProfileRole::new)
            .map(ProfileRole::init)
            .map(role -> role.setGroup(this))
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ProfileGroup that)) {
            return false;
        }
        return this.groupId.equals(that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupId);
    }

    @Override
    public String toString() {
        return "ProfileGroup{" +
            "groupId='" + this.groupId + '\'' +
            ", priority=" + this.priority +
            ", role=" + this.role +
            ", reference='" + this.reference + '\'' +
            '}';
    }
}
