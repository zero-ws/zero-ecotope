package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpMethod;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheUri extends OCache<ConcurrentMap<HttpMethod, Set<String>>> {
    Cc<String, OCacheUri> CC_SKELETON = Cc.open();

    static OCacheUri of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheUriAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheUriAmbiguity(bundle), cacheKey);
    }

    static OCacheUri of() {
        return of(null);
    }

    static Set<String> entireUri(final HttpMethod method) {
        return CC_SKELETON.get().values().stream()
            .flatMap(uri -> {
                if (Objects.isNull(method)) {
                    return uri.value().values().stream().flatMap(Set::stream);
                } else {
                    return uri.value().getOrDefault(method, Set.of()).stream();
                }
            })
            .collect(Collectors.toSet());
    }

    static Set<String> entireUri() {
        return entireUri(null);
    }

    OCacheUri add(String uri, HttpMethod method);

    OCacheUri remove(String uri, HttpMethod method);

    @Override
    default OCacheUri add(final ConcurrentMap<HttpMethod, Set<String>> uri) {
        uri.forEach((method, uriSet) -> uriSet.forEach(each -> this.add(each, method)));
        return this;
    }

    @Override
    default OCacheUri remove(final ConcurrentMap<HttpMethod, Set<String>> uri) {
        uri.forEach((method, uriSet) -> uriSet.forEach(each -> this.remove(each, method)));
        return this;
    }

    Set<String> valueUri(HttpMethod method);

    interface Tool {
        // 内部工具类，用来表示从属关系，静态调用（直接调用模式）

        static void resolve(final String uri, final HttpMethod method) {
            OCacheUri.of().add(uri, method);
        }

        static String recovery(final String requestUri, final HttpMethod method) {
            final Set<String> uris = OCacheUri.entireUri(method);
            if (Objects.isNull(uris) || uris.isEmpty()) {
                return requestUri;
            } else {
                return uris.stream().filter(uri -> Ut.uriMatch(requestUri, uri))
                    .findFirst().orElse(requestUri);
            }
        }

        static boolean isMatch(final String requestUri, final HttpMethod method) {
            Objects.requireNonNull(method);
            return OCacheUri.entireUri(method).stream().anyMatch(uri -> Ut.uriMatch(requestUri, uri));
        }
    }
}
