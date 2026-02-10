package io.zerows.extension.module.rbac.metadata.logged;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.component.authorization.Align;
import io.zerows.extension.module.rbac.component.authorization.ScDetent;
import io.zerows.platform.metadata.KRef;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data in Session for current user
 * Connect to Pool: vertx-web.sessions.habitus for each user session
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class ScUser {
    private static final Cc<String, ScUser> CC_USER = Cc.open();
    private final transient HMM<String, JsonObject> rapid;
    private final transient String habitus;
    private transient String userId;

    private ScUser(final String habitus) {
        this.habitus = habitus;
        this.rapid = HMM.of(KWeb.SESSION.MY_HABITUS);
    }

    // ------------------------- Profile Method ------------------------
    /*
     * Initialization fetchProfile roles ( User )
     * 1) UNION
     * 2) EAGER
     * 3) LAZY
     * 4) INTERSECT
     */
    private static Future<JsonObject> initRoles(final JsonObject profile, final JsonArray roles) {
        log.info("{} 初始化用户角色信息，Roles = {}", ScConstant.K_PREFIX, roles.encode());
        final List<Future<ProfileRole>> futures = new ArrayList<>();
        roles.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .map(ProfileRole::new)
            .map(ProfileRole::initAsync)
            .forEach(futures::add);
        return Fx.combineT(futures)
            .compose(ScDetent.user(profile)::procAsync);
    }

    private static Future<JsonObject> initGroups(final JsonObject profile, final JsonArray groups) {
        log.info("{} 初始化用户分组信息，Groups = {}", ScConstant.K_PREFIX, groups.encode());
        final List<Future<ProfileGroup>> futures = new ArrayList<>();
        groups.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .map(ProfileGroup::new)
            .map(ProfileGroup::initAsync)
            .forEach(futures::add);
        final KRef parentHod = new KRef();
        final KRef childHod = new KRef();
        return Fx.combineT(futures).compose(profiles -> Ux.future(profiles)
            /* 直接组 Profile */
            .compose(Align::flat)
            .compose(ScDetent.group(profile)::procAsync)
            .map(nil -> profiles)


            /* 父类组 Profile */
            .compose(Align::parent)
            .compose(parentHod::future)
            // 仅父组 Parent Only
            .compose(parents -> ScDetent.parent(profile, profiles).procAsync(parents))
            // 父组和当前组 Parent and Current
            .compose(nil -> ScDetent.inherit(profile, profiles).procAsync(parentHod.get()))
            .map(nil -> profiles)


            /* 子组模式 */
            .compose(Align::children)
            .compose(childHod::future)
            /* 仅父组 */
            .compose(parents -> ScDetent.parent(profile, profiles).procAsync(parents))
            // 父组和当前组 Parent and Current
            .compose(nil -> ScDetent.inherit(profile, profiles).procAsync(childHod.get()))
            .map(nil -> profiles)


            /* 子组模式 */
            .compose(Align::children)
            .compose(childHod::future)
            // 子组
            .compose(children -> ScDetent.children(profile, profiles).procAsync(children))
            // 子组和当前组
            .compose(nil -> ScDetent.extend(profile, profiles).procAsync(childHod.get()))
        ).map(nil -> profile);
    }

    // ------------------------- Initialized Method ------------------------
    /*
     * Create ScUser for current Logged User
     * 1. The key is calculated with `habitus` findRunning
     * 2. The data input contains
     *
     * Memory
     *      "habitus" = ScUser
     *
     * SharedPool
     * 1st Level
     *      "habitus" = {}
     * 2nd Level: {} Content
     * Create relation between session & user
     * {
     *      "user": "X_USER key field, client key/user id here",
     *      "role": [
     *          {
     *              "roleId": "X_ROLE key field",
     *              "priority": 0
     *          }
     *      ],
     *      "group":[
     *          {
     *              "groupId": "X_GROUP key field",
     *              "priority": 0
     *          }
     *      ],
     *      "habitus": "128 bit random string",
     *      "session": "session id that vert.x generated",
     *      "profile": {
     *          "name": {
     *              "PERM": [],
     *              "ROLE": []
     *          }
     *      },
     *      "view": {
     *      }
     * }
     */
    public static Future<ScUser> initProfile(final User logged) {
        final JsonObject userData = logged.principal();
        final String habitus = userData.getString(KName.HABITUS);
        return Ux.future(CC_USER.pick(() -> new ScUser(habitus), habitus)).compose(user -> {
            final JsonObject stored = userData.copy();
            stored.remove(KName.HABITUS);
            final String userId = stored.getString(KName.USER);
            return user.user(userId).set(stored);        // Start Async
        }).compose(user -> user.profile()
            // Role Profile initialized
            .compose(profile -> initRoles(profile, userData.getJsonArray(KName.ROLE)))
            // Group Profile initialized
            .compose(profile -> initGroups(profile, userData.getJsonArray(KName.GROUP)))
            // Stored
            .compose(user::profile)
            // Report
            .compose(user::report)
            // Final result
            .compose(nil -> Ux.future(user))
        );
    }

    public static ScUser login(final String habitus) {
        return CC_USER.get(habitus);
    }

    public static ScUser login(final User user) {
        final JsonObject principle = user.principal();
        final String habitus = principle.getString(KName.HABITUS);
        return login(habitus);
    }

    public static Future<Boolean> logout(final String habitus) {
        final ScUser user = CC_USER.get(habitus);
        Objects.requireNonNull(user);
        return user.logout();
    }

    private ScUser user(final String userId) {
        this.userId = userId;
        return this;
    }

    public String user() {
        return this.userId;
    }

    // ------------------------- Session Method -----------------------
    public Future<JsonObject> view() {
        return this.<JsonObject>get(KName.VIEW).compose(item -> {
            if (Objects.isNull(item)) {
                return Ux.futureJ();
            }
            return Ux.future(item);
        });
    }

    public Future<JsonObject> view(final String viewKey) {
        return this.view().compose(view -> Ux.future(view.getJsonObject(viewKey)))
            .compose(view -> {
                if (Ut.isNotNil(view)) {
                    log.debug("[ XMOD ] ScUser 缓存命中 View，Key = {}, Data = {}", viewKey, view.encode());
                }
                return Ux.future(view);
            });
    }

    public Future<JsonObject> view(final String viewKey, final JsonObject viewData) {
        return this.view().compose(view -> {
            final JsonObject stored = view.getJsonObject(viewKey, new JsonObject());
            // Deep Merge is not needed
            stored.mergeIn(viewData);
            view.put(viewKey, stored);
            return this.set(KName.VIEW, view);
        });
    }

    /*
     * profile: {
     *      "name": {
     *          "PERM": [],
     *          "ROLE": []
     *      }
     * }
     */
    public Future<JsonObject> profile() {
        return this.<JsonObject>get(KName.PROFILE)
            .map(item -> Objects.isNull(item) ? new JsonObject() : item);
    }

    public Future<JsonObject> permissions() {
        return this.profile(ScAuthKey.PROFILE_PERM);
    }

    public Future<JsonObject> roles() {
        return this.profile(ScAuthKey.PROFILE_ROLE);
    }

    public Future<JsonArray> roles(final String profileName) {
        return this.profile(ScAuthKey.PROFILE_ROLE).compose(json -> {
            log.info("[ XMOD ] ( RBAC ) 获取用户角色信息，Profile Name = {}", profileName);
            return Ux.future(json.getJsonArray(profileName, new JsonArray()));
        });
    }

    public Future<JsonObject> profile(final JsonObject profileData) {
        return this.set(KName.PROFILE, profileData);
    }

    // ------------------------- Private Method ------------------------

    private Future<JsonObject> profile(final String key) {
        return this.profile().compose(profile -> {
            final JsonObject map = new JsonObject();
            Ut.<JsonObject>itJObject(profile, (item, profileName) -> {
                final JsonArray data = item.getJsonArray(key, new JsonArray());
                map.put(profileName, data);
            });
            return Ux.future(map);
        });
    }

    private Future<Boolean> logout() {
        /*
         * Remove reference pool first
         */
        // USERS.remove(this.habitus);
        CC_USER.remove(this.habitus);
        return this.rapid.remove(this.habitus)
            .compose(nil -> Ux.future(Boolean.TRUE));
    }

    private Future<JsonObject> report(final JsonObject result) {
        log.info("[ XMOD ] ( RBAC ) 用户权限信息报表：{}", result.encodePrettily());
        return Ux.future(result);
    }

    private Future<ScUser> set(final JsonObject data) {
        return this.getStored().compose(stored -> {
            stored.mergeIn(data, true);
            return this.rapid.put(this.habitus, stored)
                .compose(nil -> Ux.future(this));
        });
    }

    private <T> Future<T> set(final String dataKey, final T value) {
        return this.getStored().compose(stored -> {
            // dataKey = findRunning, the Tool must be valid for JsonObject
            stored.put(dataKey, value);
            return this.rapid.put(this.habitus, stored)
                .compose(nil -> Ux.future(value));
        });
    }

    private Future<JsonObject> getStored() {
        return this.rapid.find(this.habitus).compose(stored -> {
            // 1st time fetch data often return null here
            if (Ut.isNil(stored)) {
                stored = new JsonObject();
            }
            return Ux.future(stored);
        });
    }

    @SuppressWarnings("unchecked")
    private <T> Future<T> get(final String dataKey) {
        return this.getStored().compose(stored -> {
            if (Ut.isNil(stored)) {
                return Ux.future();
            } else {
                return Ux.future((T) stored.getValue(dataKey));
            }
        });
    }
}
