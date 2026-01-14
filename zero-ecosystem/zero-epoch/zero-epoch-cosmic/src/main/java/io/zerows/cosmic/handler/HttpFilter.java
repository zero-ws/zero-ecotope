package io.zerows.cosmic.handler;

import io.r2mo.function.Fn;
import io.vertx.core.VertxException;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.cortex.exception._40051Exception500FilterContext;
import io.zerows.cosmic.bootstrap.AckFailure;
import io.zerows.epoch.web.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class HttpFilter implements Filter {
    private boolean isNexted = false;
    private RoutingContext context;

    @Override
    public void init(final RoutingContext context) {
        this.context = context;
        this.init();
    }

    protected void put(final String key, final Object value) {
        this.context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(final String key) {
        final Object reference = this.context.get(key);
        return null == reference ? null : (T) reference;
    }

    @Override
    public void doFilter(final HttpServerRequest request,
                         final HttpServerResponse response) throws VertxException {
        final HttpMethod method = request.method();

        try {
            if (HttpMethod.GET == method) {
                this.doGet(request, response);
            } else if (HttpMethod.POST == method) {
                this.doPost(request, response);
            } else if (HttpMethod.PUT == method) {
                this.doPut(request, response);
            } else if (HttpMethod.DELETE == method) {
                this.doDelete(request, response);
            }
            this.doFilterContinue(request, response);
        } catch (final Throwable ex) {
            // 直接抛出异常，转交 Handler
            AckFailure.of().reply(this.context, ex);
        }
    }

    private void doFilterContinue(final HttpServerRequest request,
                                  final HttpServerResponse response) {
        // If response end it means that it's not needed to move next.
        if (this.isNexted) {
            return;
        }
        if (response.ended()) {
            return;
        }

        // 标记放行
        this.isNexted = true;
        this.context.next();
    }

    protected Session getSession() {
        return this.context.session();
    }

    protected Map<String, Cookie> getCookies() {
        return this.context.request()
            .cookies()
            .stream()
            .collect(Collectors.toMap(Cookie::getName, cookie -> cookie));
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    public void init() {
        Fn.jvmKo(Objects.isNull(this.context), _40051Exception500FilterContext.class);
    }
}
