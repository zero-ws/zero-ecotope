package io.zerows.extension.skeleton.common;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.skeleton.spi.ExUser;
import io.zerows.extension.skeleton.spi.ScOrbit;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/*
 * Key generated for uniform app.zero.cloud
 */
@Slf4j
class KeCache {

    static String keyView(final String method, final String uri, final KView view) {
        /*
         * session-POST:uri:position/name
         */
        return "session-" + method + ":" + uri + ":" + view.position() + "/" + view.view();
    }

    static String keyAuthorized(final String method, final String uri) {
        return "authorized-" + method + ":" + uri;
    }

    static String keyResource(final String method, final String uri) {
        return "resource-" + method + ":" + uri;
    }

    static String uri(final String uri, final String requestUri) {
        final JsonObject parameters = new JsonObject();
        parameters.put(KName.URI, uri);
        parameters.put(KName.URI_REQUEST, requestUri);
        // SPI: ScOrbit
        return HPI.of(ScOrbit.class).waitUntil(
            orbit -> orbit.analyze(parameters),
            () -> uri
        );
    }

    static String uri(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final HttpMethod method = request.method();
        final String requestUri = OCacheUri.Tool.recovery(request.path(), method);
        return uri(requestUri, request.path());
    }

    static String keyView(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final String uri = uri(context);
        /* Cache Data */
        final String literal = request.getParam(KName.VIEW);
        /* Url Encoding / Decoding */
        final KView vis = KView.create(literal);
        final String cacheKey = keyView(request.method().name(), uri, vis);
        /* Cache Data */
        log.debug("{} 输入视图 View = {} / By = {}, 尝试命中缓存：uri = {}, method = {}",
            KeConstant.K_PREFIX_WEB, literal, cacheKey, uri, request.method().name());
        return cacheKey;
    }

    /*
     * Extract data for `__user`, it could not be core-framework, but for extension only
     * because it contains following tables:
     * - S_USER,
     * - S_GROUP,
     * - S_ROLE,
     * - E_TEAM,
     * - E_DEPT,
     * - E_EMPLOYEE
     *
     * The input InJson is as following:
     *
     * {
     *     "user": [
     *     ],
     *     "role": [
     *     ],
     *     "group": [
     *     ],
     *     "dept": [
     *     ],
     *     "team": [
     *     ]
     * }
     *
     * To:
     * {
     *     "user": {},
     *     "role": [],
     *     "group": [],
     *     "dept": {},
     *     "team": {},
     *     "__data": {
     *         "user": {},
     *         "role": [],
     *         "group": [],
     *         "dept": {}
     *         "team": {}
     *     }
     * }
     */
    static Future<JsonObject> umUser(final JsonObject input, final JsonObject config) {
        /*
         * Replace the `__user` node for script usage etc.
         */
        final JsonObject output = Ut.valueJObject(input).copy();
        final JsonObject replaced = new JsonObject();
        return umUserInternal(input, config)
            .compose(user -> {
                replaced.put(KName.USER, user);
                return Ux.futureT();
            })
            .compose(nil -> {
                output.put(KName.__.USER, replaced);
                return Ux.future(output);
            });
    }

    // =============================== User Extract =============================
    private static Future<JsonObject> umUserInternal(final JsonObject input, final JsonObject config) {
        final JsonObject dataO = Ut.aiDataO(input);
        final JsonObject dataN = Ut.aiDataN(input);
        final KRef userN = new KRef();
        final JsonArray users = Ut.valueJArray(config, KName.USER);
        return umUser(dataN, users)
            .compose(userN::future)
            .compose(nil -> umUser(dataO, users))
            .compose(userO -> {
                JsonObject userJ = userN.get();
                userJ = userJ.copy();
                userJ.put(KName.__.DATA, userO);
                return Ux.future(userJ);
            });
    }

    private static Future<JsonObject> umUser(final JsonObject input, final JsonArray users) {
        final Set<String> keySet = new HashSet<>();
        Ut.itJArray(users, String.class, (field, index) -> {
            final Object value = input.getValue(field);
            if (value instanceof JsonArray) {
                keySet.addAll(Ut.toSet((JsonArray) value));
            } else if (value instanceof String) {
                keySet.add((String) value);
            }
        });
        return HPI.of(ExUser.class).waitAsync(
            stub -> stub.mapUser(keySet, true).compose(userMap -> {
                final JsonObject normalized = new JsonObject();
                Ut.itJArray(users, String.class, (field, index) -> {
                    final Object value = input.getValue(field);
                    if (value instanceof JsonArray) {
                        final Set<String> vSet = Ut.toSet((JsonArray) value);
                        final JsonArray userA = new JsonArray();
                        vSet.forEach(userKey -> {
                            final JsonObject userJ = userMap.getOrDefault(userKey, null);
                            if (Ut.isNotNil(userJ)) {
                                userA.add(userJ);
                            }
                        });
                        normalized.put(field, userA);                       // Replace
                    } else if (value instanceof String) {                   // Replace
                        final String userKey = (String) value;
                        final JsonObject userJ = userMap.getOrDefault(userKey, null);
                        if (Ut.isNotNil(userJ)) {
                            normalized.put(field, userJ);
                        } else {
                            normalized.put(field, new JsonObject());        // Replace
                        }
                    } else {
                        normalized.put(field, new JsonObject());            // Empty Replace
                    }
                });
                return Ux.future(normalized);
            }),
            () -> input
        );
    }
}
