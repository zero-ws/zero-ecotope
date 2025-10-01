package io.zerows.epoch.corpus.web.client;

import io.r2mo.function.Fn;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.app.KIntegrationApi;
import io.zerows.epoch.program.Ut;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class LegacyEmitter extends AbstractEmitter {
    LegacyEmitter(final KIntegration integration) {
        super(integration);
    }

    /*
     * Set trusted connect
     */
    @Override
    protected void initialize() {
        final SSLContext context = this.sslContext();
        if (Objects.nonNull(context)) {

            /* Initialize HttpsURLConnection */
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        }
    }

    @Override
    public String request(final String apiKey, final JsonObject params, final MultiMap headers) {
        /*
         * Read KIntegrationApi object
         */
        final KIntegrationApi request = this.integration().createRequest(apiKey);
        /*
         * Encrypt content with public key of RSA
         * Replace the method `getPublicKeyFile` with `getPublicKey` for content extracting
         */
        final String content = Ut.encryptRSAP(params.encode(), this.integration().getPublicKey());
        /*
         * Send request to read String response here.
         */
        return this.send(request.getPath(), request.getMethod(), MediaType.APPLICATION_JSON_TYPE, content);
    }

    private String send(final String uri, final HttpMethod method, final MediaType mediaType, final String content) {
        return Fn.jvmOr(() -> {
            this.logger().info(INFO.HTTP_REQUEST, uri, method, content);
            final String contentType = Objects.isNull(mediaType) ? MediaType.APPLICATION_JSON : mediaType.toString();

            /* Cert trusted */
            this.initialize();

            /* Create new connect */
            final URL url = new URI(uri).toURL();
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            /* Set options for current connection */
            conn.setRequestMethod(method.name());
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            /* Input resonse stream to String */
            final PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(content);
            out.flush();
            out.close();

            /* Convert to content */
            final String response = Ut.ioString(conn.getInputStream());
            final String normalized = new String(response.getBytes(), StandardCharsets.UTF_8);
            this.logger().info(INFO.HTTP_RESPONSE, normalized);
            return normalized;
        });
    }
}
