package io.zerows.epoch.basicore.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.impl.Origin;
import io.zerows.epoch.constant.KWeb;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.platform.annotations.ClassYml;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/*
 * Cors configuration here.
 * The data came from `secure -> cors`
 */
@Data
@ClassYml
public class CorsOptions implements Serializable {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);
    private static CorsOptions INSTANCE;
    @JsonProperty("allow-credentials")
    private Boolean credentials = Boolean.FALSE;
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    @JsonProperty("allowed-methods")
    private JsonArray methods = new JsonArray();
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    @JsonProperty("allowed-headers")
    private JsonArray headers = new JsonArray();
    /*
     * Modified from 4.3.1, here the origin has been modified
     * From Vert.x 4.3.1, instead the origin must be configured
     */
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    @JsonProperty("allowed-origins")
    private JsonArray origin = new JsonArray();

    @JsonProperty("max-age")
    private int maxAge;

    public CorsOptions() {
        this.initMethods(this.methods);

        this.initHeaders(this.headers);
    }

    public JsonArray getMethods() {
        if (this.methods.isEmpty()) {
            this.initMethods(this.methods);
        }
        return this.methods;
    }

    private void initMethods(final JsonArray methods) {
        methods.clear();
        methods.add(HttpMethod.GET.name())
            .add(HttpMethod.POST.name())
            .add(HttpMethod.PUT.name())
            .add(HttpMethod.DELETE.name())
            .add(HttpMethod.OPTIONS.name());
    }

    private void initHeaders(final JsonArray headers) {
        headers.clear();
        headers.add(HttpHeaders.AUTHORIZATION.toString())
            .add(HttpHeaders.ACCEPT.toString())
            .add(HttpHeaders.CONTENT_DISPOSITION.toString())
            .add(HttpHeaders.CONTENT_ENCODING.toString())
            .add(HttpHeaders.CONTENT_LENGTH.toString())
            .add(HttpHeaders.CONTENT_TYPE.toString())
            /* 第三方对接 */
            .add(HttpHeaders.EXPIRES.toString())
            .add("expireSeconds")
            /* User defined header */
            .add(KWeb.HEADER.X_APP_ID)
            .add(KWeb.HEADER.X_APP_KEY)
            .add(KWeb.HEADER.X_SIGMA)
            .add(KWeb.HEADER.X_LANG)
            .add(KWeb.HEADER.X_TENANT_ID)
            .add(KWeb.HEADER.X_SESSION_ID);
    }

    public JsonArray getHeaders() {
        if (this.headers.isEmpty()) {
            this.initHeaders(this.headers);
        }
        return this.headers;
    }

    // 非序列化快速方法


    public Set<HttpMethod> withMethods() {
        return this.getMethods().stream()
            .filter(Objects::nonNull)
            .map(item -> (String) item)
            .map(Ut::toMethod)
            .collect(Collectors.toSet());
    }

    public Set<String> withHeaders() {
        return this.getHeaders().stream()
            .filter(Objects::nonNull)
            .map(item -> (String) item)
            .collect(Collectors.toSet());
    }

    public Set<String> withOrigins() {
        return Ut.itJArray(this.origin, String.class)
            .filter(Objects::nonNull)
            .filter(Origin::isValid)
            .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "CorsOptions{" +
            "credentials=" + this.credentials +
            ", methods=" + this.methods +
            ", headers=" + this.headers +
            ", origin='" + this.origin + '\'' +
            '}';
    }
}
