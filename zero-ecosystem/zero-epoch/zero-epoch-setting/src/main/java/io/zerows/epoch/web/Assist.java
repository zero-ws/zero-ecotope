package io.zerows.epoch.web;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.commune.Vis;
import io.zerows.support.Ut;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * Envelop Assist for additional Data here.
 */
class Assist implements Serializable {
    private final Map<String, Object> context = new HashMap<>();
    /* Fixed header null dot */
    private MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    private User user;
    private String uri;
    private HttpMethod method;
    private Session session;
    private RoutingContext reference;

    <T> T getContextData(final String key, final Class<?> clazz) {
        T reference = null;
        if (this.context.containsKey(key)) {
            reference = Rib.deserialize(this.context.get(key), clazz);
        }
        return reference;
    }

    @SuppressWarnings("all")
    String principal(final String field) {
        final JsonObject credential = this.user.principal();
        return Ut.valueString(credential, field);
    }

    void bind(final RoutingContext context) {
        this.reference = context;
    }

    RoutingContext reference() {
        return this.reference;
    }

    User user() {
        return this.user;
    }

    void user(final User user) {
        this.user = user;
    }

    MultiMap headers() {
        return this.headers;
    }

    JsonObject headersX() {
        final JsonObject headerData = new JsonObject();
        this.headers.names().stream()
            /* Up case is OK */
            .filter(field -> field.startsWith(KWeb.HEADER.PREFIX)
                /* Lower case is also Ok */
                || field.startsWith(KWeb.HEADER.PREFIX.toLowerCase(Locale.getDefault())))
            /*
             * Data for header
             * X-App-Id -> appId
             * X-App-Key -> appKey
             * X-Sigma -> sigma
             */
            .forEach(field -> {
                /*
                 * Lower / Upper are both Ok
                 */
                final String found = KWeb.HEADER.PARAM_MAP.keySet()
                    .stream().filter(field::equalsIgnoreCase)
                    .findFirst().map(KWeb.HEADER.PARAM_MAP::get).orElse(null);
                if (Ut.isNotNil(found)) {
                    headerData.put(found, this.headers.get(field));
                }
            });
        return headerData;
    }

    void headers(final MultiMap headers) {
        this.headers = headers;
    }

    Session session() {
        return this.session;
    }

    void session(final Session session) {
        this.session = session;
    }

    String uri() {
        return this.uri;
    }

    void uri(final String uri) {
        this.uri = uri;
    }

    HttpMethod method() {
        return this.method;
    }

    void method(final HttpMethod method) {
        this.method = method;
    }

    void context(final Map<String, Object> data) {
        this.context.clear();
        this.context.putAll(data);
    }

    JsonObject requestSmart() {
        final Object[] arguments = this.reference.get(KWeb.ARGS.REQUEST_CACHED);
        final JsonObject argumentJ = new JsonObject();
        // Path + Query ( Low Priority )
        // 如果出现 view 参数，则需要被 Vis 覆盖
        this.reference.pathParams().forEach(argumentJ::put);
        this.reference.queryParams().forEach(argumentJ::put);
        // Iterate each arguments to check the `JsonObject`
        Arrays.stream(arguments).forEach(value -> {
            if (value instanceof final Vis vis) {
                // Vis ( Inherit from JsonObject )
                argumentJ.put(KName.VIEW, vis.view());
                argumentJ.put(KName.POSITION, vis.position());
            } else if (value instanceof final JsonObject json) {
                argumentJ.mergeIn(json, false);
            }
        });
        return argumentJ;
    }

    @Override
    public String toString() {
        return "Assist{" +
            // Stack Overflow here
            // "context=" + this.context +
            ", headers=" + this.headers +
            ", user=" + this.user +
            ", uri='" + this.uri + '\'' +
            ", method=" + this.method +
            ", session=" + this.session +
            '}';
    }
}
