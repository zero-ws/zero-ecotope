package io.zerows.metadata.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.integrated.jackson.databind.JsonObjectSerializer;
import io.zerows.support.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/*
 * KIntegrationApi for api description
 */
public class KIntegrationApi implements Serializable {
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

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

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

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setMethod(final HttpMethod method) {
        this.method = method;
    }

    public JsonObject getHeaders() {
        return this.headers;
    }

    public void setHeaders(final JsonObject headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KIntegrationApi)) {
            return false;
        }
        final KIntegrationApi request = (KIntegrationApi) o;
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
