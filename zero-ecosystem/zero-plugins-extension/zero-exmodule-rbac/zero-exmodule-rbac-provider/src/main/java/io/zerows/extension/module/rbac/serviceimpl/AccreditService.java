package io.zerows.extension.module.rbac.serviceimpl;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.component.log.LogOf;
import io.zerows.plugins.cache.Rapid;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.DataBound;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.metadata.logged.ScResource;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.component.acl.rapier.Quinn;
import io.zerows.extension.module.rbac.common.ScAuthMsg;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.common.ScOwner;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.module.rbac.exception._80209Exception404ActionMissing;
import io.zerows.extension.module.rbac.exception._80210Exception404ResourceMissing;
import io.zerows.extension.module.rbac.exception._80211Exception403ActionDinned;
import io.zerows.extension.module.rbac.servicespec.AccreditStub;
import io.zerows.extension.module.rbac.servicespec.ActionStub;
import io.zerows.extension.skeleton.common.enums.OwnerType;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;

import static io.zerows.extension.module.rbac.boot.Sc.LOG;

public class AccreditService implements AccreditStub {
    private final static LogOf LOGGER = LogOf.get(AccreditService.class);

    @Inject
    private transient ActionStub stub;

    /*
     * Permission
     * {
     *      "profile1": [],
     *      "profile2": []
     * }
     */
    @Override
    public Future<JsonObject> profile(final User user) {
        final ScUser scUser = ScUser.login(user);
        return scUser.permissions();
    }

    @Override
    public Future<JsonObject> resource(final JsonObject requestData) {
        final ScResource request = ScResource.create(requestData);
        // First Phase
        return Rapid.<String, JsonObject>object(ScConstant.POOL_RESOURCES)
            .cached(request.key(), () -> {
                /* Fetch Action */
                final KRef actionHod = new KRef();
                // Action Checking
                return this.fetchAction(request)
                    .compose(action -> this.inspectAction(request, action))
                    .compose(actionHod::future)
                    // Resource Checking
                    .compose(action -> this.stub.fetchResource(action.getResourceId()))
                    .compose(resource -> this.inspectResource(request, actionHod.get(), resource))
                    // Level Checking
                    .compose(resource -> this.inspectLevel(resource, actionHod.get()))
                    // Resource Data Processing
                    .compose(resource -> this.inspectData(resource, actionHod.get()));
            })
            .compose(stored -> this.inspectView(requestData, request, stored))
            // Extract `data` node
            .compose(stored -> Ux.future(stored.getJsonObject(KName.DATA)));
    }

    private Future<JsonObject> inspectView(final JsonObject requestData, final ScResource resource,
                                           final JsonObject response) {
        final String habitus = requestData.getString(KName.HABITUS);
        final String keyView = resource.keyView();
        final ScUser user = ScUser.login(habitus);
        if (Objects.isNull(user)) {
            return Future.succeededFuture(new JsonObject());
        }
        return user.view(keyView).compose(viewData -> {
            if (Objects.nonNull(viewData)) {
                return Ux.future(response);
            }
            /*
             * Fetch DataBound by:
             * request -> userId, session, fetchProfile
             *
             * 提取RBAC配置信息（资源池的缓存）
             */
            final KRef resourceRef = new KRef();
            return Rapid.<String, JsonObject>object(ScConstant.POOL_RESOURCES).read(resource.key()).compose(data -> {
                final SResource resourceT = Ux.fromJson(data.getJsonObject(KName.RECORD), SResource.class);
                return resourceRef.future(resourceT);
            }).compose(resourceT -> {
                /*
                 * No Personal View
                 * There is no matrix stored into database related to current user.
                 * Then findRunning all role related matrices instead of current matrix.
                 */
                final String profileName = Sc.valueProfile(resourceT);
                return user.roles(profileName);
            }).compose(roles -> {
                /*
                 * Fetch Role View
                 * It means that there is defined user resource instead of role resource.
                 * In this situation, return to user's resource matrix directly.
                 * 此处会预处理数据，主要目的是为构造 ScOwner，一旦构造完成后，会智能提取相关数据
                 * 1）如果不绑定 roles，则等价于直接从 ScOwner 中提取数据
                 * 2）如果绑定了 roles，则提取用户视图为空时会提取角色视图
                 */
                final ScOwner owner = new ScOwner(user.user(), OwnerType.USER);
                owner.bind(resource.view()).bind(Ut.toSet(roles));
                final SResource resourceT = resourceRef.get();
                return Quinn.vivid().<DataBound>fetchAsync(resourceT, owner)
                    .compose(bound -> user.view(keyView, bound.toJson()))
                    .compose(nil -> Ux.future(response));
            });
        });
    }

    /*
     * {
     *      "key": "profileKey",
     *      "data": {
     *          "profileKey": {
     *
     *          }
     *      },
     *      "record": {
     *          SResource Data Structure ( InJson )
     *      }
     * }
     */
    private Future<JsonObject> inspectData(final SResource resource, final SAction action) {
        // profile
        final String profileKey = Sc.valueProfile(resource);
        final JsonArray permissions = new JsonArray().add(action.getPermissionId());
        // resource data
        final JsonObject stored = new JsonObject();
        stored.put(KName.RECORD, Ut.serializeJson(resource));
        stored.put(KName.KEY, profileKey);
        stored.put(KName.DATA, new JsonObject().put(profileKey, permissions));

        return Ux.future(stored);
    }

    private Future<SResource> inspectLevel(final SResource resource, final SAction action) {
        final Integer required = resource.getLevel();
        final Integer actual = action.getLevel();
        if (actual < required) {
            return FnVertx.failOut(_80211Exception403ActionDinned.class, required, actual);
        } else {
            LOG.Credit.debug(LOGGER, ScAuthMsg.CREDIT_LEVEL, action.getLevel(), resource.getLevel());
            return Future.succeededFuture(resource);
        }
    }

    /*
     * 1. Whether action is existing
     * If action missing, throw 404 exception
     */
    private Future<SAction> inspectAction(final ScResource request, final SAction action) {
        if (Objects.isNull(action)) {
            final String requestUri = request.method() + " " + request.uri();
            return FnVertx.failOut(_80209Exception404ActionMissing.class, requestUri);
        } else {
            LOG.Credit.debug(LOGGER, ScAuthMsg.CREDIT_ACTION, request.uriRequest(), request.method(), request.uri());
            return Future.succeededFuture(action);
        }
    }

    /*
     * 2. Whether resource is existing
     * If resource missing, throw 404 exception
     */
    private Future<SResource> inspectResource(final ScResource request, final SAction action, final SResource resource) {
        if (Objects.isNull(resource)) {
            final String requestUri = request.method() + " " + request.uri();
            return FnVertx.failOut(_80210Exception404ResourceMissing.class, action.getResourceId(), requestUri);
        } else {
            LOG.Credit.debug(LOGGER, ScAuthMsg.CREDIT_RESOURCE, resource.getKey());
            return Future.succeededFuture(resource);
        }
    }

    private Future<SAction> fetchAction(final ScResource resource) {
        return this.stub.fetchAction(
            resource.uri(),                         // Normalized Uri
            resource.method(),
            resource.sigma()
        ).compose(action -> {
            if (Objects.nonNull(action)) {
                /* Action Found */
                return Ux.future(action);
            }
            /* Check Normalized, action = null */
            if (resource.isNormalized()) {
                return this.stub.fetchAction(
                    resource.uriRequest(),          // Request Uri
                    resource.method(),
                    resource.sigma()
                );
            }
            return Ux.future();
        });
    }
}
