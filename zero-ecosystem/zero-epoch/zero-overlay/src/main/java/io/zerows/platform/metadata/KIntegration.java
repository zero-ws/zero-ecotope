package io.zerows.platform.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.constant.VString;
import io.zerows.specification.atomic.HCopier;
import io.zerows.specification.atomic.HJson;
import io.zerows.support.base.UtBase;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/*
 * DTO for third part integration basic configuration instead of other
 * {
 *      "endpoint": "http://www.demo.cn/ws/api/",
 *      "port": 1234,
 *      "username": "lang",
 *      "password": "xxxx",
 *      "hostname": "www.demo.cn or 192.168.0.12",
 *      "publicKeyFile": "public key path",
 *      "apis":{
 *          "get.username": {
 *              "method": "POST",
 *              "uri": "/uri/getinfo",
 *              "headers": {}
 *          },
 *          "post.mock": {
 *              "method": "GET",
 *              "uri": "/uri/getinfo",
 *              "headers": {}
 *          }
 *      },
 *      "options":{
 *
 *      }
 * }
 */
@Data
public class KIntegration implements HJson, Serializable, HCopier<KIntegration> {

    private final ConcurrentMap<String, Api> apis
        = new ConcurrentHashMap<>();
    /*
     * Restful / Web Service information ( such as jdbcUrl )
     * The ofMain service should be: endpoint + api ( KIntegrationApi )
     */
    private String endpoint;
    private Integer port;
    private String username;
    private String protocol;
    /*
     * SSL enabled, these two fields stored
     * 1) publicKeyFile
     * 2) Authentication
     */
    private String password;
    private String hostname;
    private String publicKeyFile;

    /*
     * options for configuration of JSON formatFail
     */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject options = new JsonObject();
    @JsonIgnore
    private ConcurrentMap<String, KDictUse> epsilon = new ConcurrentHashMap<>();
    @JsonIgnore
    private String vendorConfig;
    @JsonIgnore
    private String vendor;

    public String getPublicKey() {
        if (UtBase.isNil(this.publicKeyFile)) {
            return VString.EMPTY;
        }
        return UtBase.ioString(this.publicKeyFile);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOption(final String optionKey) {
        final JsonObject options = this.getOptions();
        final Object value = options.getValue(optionKey);
        return (T) value;
    }

    public <T> T getOption(final String optionKey, final T defaultValue) {
        final T result = this.getOption(optionKey);
        return Objects.isNull(result) ? defaultValue : result;
    }

    /*
     * Control for debug
     */
    public void mockOn() {
        this.options.put("debug", true);
    }

    public void mockOff() {
        this.options.put("debug", false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KIntegration> CHILD copy() {
        final KIntegration integration = new KIntegration();
        final JsonObject data = this.toJson().copy();
        integration.fromJson(data);
        return (CHILD) integration;
    }

    @Override
    public JsonObject toJson() {
        return UtBase.serializeJson(this);
    }

    @Override
    public void fromJson(final JsonObject data) {
        this.endpoint = data.getString("endpoint");
        this.hostname = data.getString("hostname");
        this.username = data.getString("username");
        this.password = data.getString("password");
        this.protocol = data.getString("protocol");
        this.port = data.getInteger("port");
        this.publicKeyFile = data.getString("publicKeyFile");
        /*
         * KIntegration Request
         */
        final JsonObject apis = data.getJsonObject("apis");
        if (UtBase.isNotNil(apis)) {
            UtBase.<JsonObject>itJObject(apis, (json, field) -> {
                final Api request = UtBase.deserialize(json, Api.class);
                this.apis.put(field, request);
            });
        }
        final JsonObject options = data.getJsonObject("options");
        if (UtBase.isNotNil(options)) {
            this.options = options.copy();
        }
    }

    public Api createRequest(final String key) {
        final Api request = new Api();
        final Api original = this.apis.get(key);
        request.setHeaders(original.getHeaders().copy());
        request.setMethod(original.getMethod());
        if (0 <= original.getPath().indexOf('`')) {
            /*
             * Expression Path
             */
            request.setExecutor(this.endpoint, original.getPath());
        } else {
            /*
             * Standard Path
             */
            request.setPath(this.endpoint + original.getPath());
        }
        return request;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final KIntegration that)) {
            return false;
        }
        return this.endpoint.equals(that.endpoint) &&
            this.port.equals(that.port) &&
            this.protocol.equals(that.protocol) &&
            this.username.equals(that.username) &&
            this.password.equals(that.password) &&
            this.hostname.equals(that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.endpoint, this.port, this.protocol, this.username, this.password, this.hostname);
    }

    @Override
    public String toString() {
        return "KIntegration{" +
            "apis=" + this.apis +
            ", vendor=" + this.vendorConfig +
            ", endpoint='" + this.endpoint + '\'' +
            ", port=" + this.port +
            ", protocol='" + this.protocol + '\'' +
            ", username='" + this.username + '\'' +
            ", password='" + this.password + '\'' +
            ", hostname='" + this.hostname + '\'' +
            ", publicKeyFile='" + this.publicKeyFile + '\'' +
            '}';
    }

    /*
     * KIntegrationApi for api description
     */
    @Data
    public static class Api implements Serializable {
        /*
         * Http uri definition here.
         */
        private String path;
        /*
         * Http method
         */
        private HttpMethod method;
        /*
         * Some specific situation that required headers
         */
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject headers = new JsonObject();

        @JsonIgnore
        private Function<JsonObject, String> executor;

        public String getPath(final JsonObject params) {
            if (Objects.nonNull(this.executor)) {
                final String delayPath = this.executor.apply(params);
                this.path = delayPath;
                return delayPath;
            } else {
                return null;
            }
        }

        public void setExecutor(final String endpoint, final String expr) {
            this.executor = params -> {
                final JsonObject normalized = UtBase.valueJObject(params);
                final String result = UtBase.fromExpression(expr, normalized);
                return endpoint + result;
            };
        }

        public boolean isExpr() {
            return Objects.nonNull(this.executor);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof final Api request)) {
                return false;
            }
            return this.path.equals(request.path) &&
                this.method == request.method;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.path, this.method);
        }

        @Override
        public String toString() {
            return "KIntegrationApi{" +
                "path='" + this.path + '\'' +
                ", method=" + this.method +
                ", headers=" + this.headers +
                '}';
        }
    }
}
