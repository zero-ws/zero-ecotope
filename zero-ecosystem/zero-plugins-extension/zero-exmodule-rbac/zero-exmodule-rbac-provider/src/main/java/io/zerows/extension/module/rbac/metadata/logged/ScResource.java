package io.zerows.extension.module.rbac.metadata.logged;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.module.rbac.boot.MDRBACManager;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.extension.skeleton.common.Ke;

/**
 * For annotation @AuthorizedResource to stored resource data structure
 * 1. The key: resource-[method]-[uri]
 * 2. The profile key could be calculated
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ScResource {
    private static final ScConfig CONFIG = MDRBACManager.of().config();

    private transient final String resourceKey;
    private transient final String uri;
    private transient final String requestUri;
    private transient final String sigma;
    private transient final KView view;
    private transient final HttpMethod method;

    /*
     * 1. Fetch resource from cache
     * 2. When cache is null, fetch data from database here.
     * {
     *     "metadata" : {
     *         "uri" : "xxx",
     *         "requestUri" : "xxx",
     *         "method" : "GET"
     *     },
     *     "access_token" : "xxxxx",
     *     "headers" : {
     *         "X-Sigma" : "xxx"
     *     },
     *     "options" : { }
     * }
     */
    private ScResource(final JsonObject data) {
        final JsonObject metadata = data.getJsonObject(ScAuthKey.F_METADATA);
        final String uri = metadata.getString(ScAuthKey.F_URI);
        this.requestUri = metadata.getString(ScAuthKey.F_URI_REQUEST);
        this.method = HttpMethod.valueOf(metadata.getString(ScAuthKey.F_METHOD));
        /*
         * Extension for orbit
         */
        this.uri = Ke.uri(uri, this.requestUri);
        this.view = KView.smart(metadata.getValue(KName.VIEW));
        /*
         * Support multi applications
         */
        if (CONFIG.getSupportMultiApp()) {
            final JsonObject headers = data.getJsonObject(ScAuthKey.F_HEADERS);
            this.sigma = headers.getString(KWeb.HEADER.X_SIGMA);
        } else {
            this.sigma = null;
        }
        this.resourceKey = Ke.keyResource(this.method.name(), this.uri);
    }

    public static ScResource create(final JsonObject data) {
        return new ScResource(data);
    }

    public String uri() {
        return this.uri;
    }

    public String uriRequest() {
        return this.requestUri;
    }

    public String sigma() {
        return this.sigma;
    }

    public KView view() {
        return this.view;
    }

    public String key() {
        return this.resourceKey;
    }

    public String keyView() {
        return Ke.keyView(this.method.name(), this.uri, this.view);
    }

    public HttpMethod method() {
        return this.method;
    }

    public boolean isNormalized() {
        return !this.requestUri.equals(this.uri);
    }
}
