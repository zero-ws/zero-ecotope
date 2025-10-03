package io.zerows.epoch.corpus.web.client;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.based.exception._15002Exception500JexlExpression;
import io.zerows.component.log.OLog;
import io.zerows.metadata.app.KIntegration;
import io.zerows.metadata.app.KIntegrationApi;
import io.zerows.constant.VString;
import io.zerows.epoch.corpus.web.exception._60046Exception500RequestConfig;
import io.zerows.epoch.corpus.web.exception._60047Exception501HttpClientNull;
import io.zerows.epoch.program.Ut;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class AbstractRotator implements Rotator {
    private final transient KIntegration integration;
    private transient CloseableHttpClient client;

    public AbstractRotator(final KIntegration integration) {
        this.integration = integration;
    }

    @Override
    public Rotator bind(final CloseableHttpClient client) {
        this.client = client;
        return this;
    }

    /*
     * The specific method for sub-class consume
     * - configRequest：configure `RequestConfig` object
     * - configPath：configure `path` in http request
     */
    protected RequestConfig configRequest() {
        final Integer connectTimeout =
            this.integration.getOption("timeout.connect", 5000);
        final Integer requestTimeout =
            this.integration.getOption("timeout.request", 5000);
        final Integer socketTimeout =
            this.integration.getOption("timeout.socket", 5000);
        return RequestConfig.custom()
            .setConnectTimeout(connectTimeout)
            .setConnectionRequestTimeout(requestTimeout)
            .setSocketTimeout(socketTimeout)
            .build();
    }

    protected String configPath(final KIntegrationApi request, final JsonObject params) {
        final String exprPath = request.getPath();
        if (request.isExpr()) {
            /*
             * The path contains `expression` such as
             *
             * `/path/${name}/user/${id}` mode
             *
             * In this situation, zero will parse this string
             */
            try {
                return request.getPath(params);
            } catch (final _15002Exception500JexlExpression ex) {
                ex.printStackTrace();
                throw new _60046Exception500RequestConfig(request, params.encode());
            }
        } else {
            if (Ut.isNil(exprPath)) {
                throw new _60046Exception500RequestConfig(request, params.encode());
            } else {
                /*
                 * No paring
                 */
                return exprPath;
            }
        }
    }

    protected void configHeader(final HttpRequestBase request, final JsonObject headers) {
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
        if (Ut.isNotNil(headers)) {
            headers.stream()
                .filter(item -> Objects.nonNull(item.getValue()))
                .forEach(item -> request.addHeader(item.getKey(), item.getValue().toString()));
        }
    }

    /*
     * Data processing method
     * - dataJson：Build json request of body, convert to StringEntity
     * - dataString：Build string content response here.
     */
    protected StringEntity dataJson(final JsonObject data) {
        final JsonObject normalized = Ut.valueJObject(data);

        /*
         *  Here the secondary argument will be in Chinese ??? confused characters.
         *  We must provide "utf8" as default encoding method of body
         *  */
        final StringEntity body = new StringEntity(normalized.encode(), StandardCharsets.UTF_8);
        body.setContentEncoding(StandardCharsets.UTF_8.name());
        body.setContentType(KWeb.ARGS.V_CONTENT_TYPE);
        return body;
    }

    protected String dataString(final HttpResponse response) throws IOException {
        if (Objects.isNull(response)) {
            this.logger().info("Empty Http Response");
            return VString.EMPTY;
        } else {
            final int statusLine = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusLine) {
                final String result = EntityUtils.toString(response.getEntity());
                this.logger().info("Success response: {0}", result);
                return result;
            } else {
                final String result = EntityUtils.toString(response.getEntity());
                this.logger().info("Failure response: {0}, code: {1}", result, statusLine);
                return result;
            }
        }
    }

    protected CloseableHttpClient client() {
        if (Objects.isNull(this.client)) {
            throw new _60047Exception501HttpClientNull();
        }
        return this.client;
    }

    /*
     * Send request with body or without body
     * - sendEntity
     * - sendUrl
     */
    protected String sendEntity(final HttpEntityEnclosingRequestBase request,
                                final HttpEntity body,
                                final JsonObject headers) {
        /* RequestConfig */
        final RequestConfig config = this.configRequest();
        request.setConfig(config);
        request.setEntity(body);

        /* default http header */
        this.configHeader(request, headers);

        /* send request */
        return Fn.jvmOr(() -> {
            final HttpResponse httpResponse = this.client().execute(request);

            /* build response here */
            return this.dataString(httpResponse);
        });
    }

    protected String sendUrl(final HttpRequestBase request, final JsonObject headers) {
        /* RequestConfig */
        final RequestConfig config = this.configRequest();
        request.setConfig(config);

        /* default http header */
        this.configHeader(request, headers);

        /* send request */
        return Fn.jvmOr(() -> {
            final HttpResponse httpResponse = this.client().execute(request);

            /* Final data */
            return this.dataString(httpResponse);
        });
    }

    protected OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
