package io.zerows.epoch.corpus.model.store;

import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.corpus.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * URI 路由管理器，管理完整路由表
 * <pre><code>
 *     内置方法
 *     GET = uri1, uri2, uri3 ....
 *     POST = uri1, uri2, uri3 ....
 *     提供各种不同的解析方法和相关模式，全局只有一个，而每个 Osgi 也只有一个，Osgi 级别的区分在于
 *     Global = META        = OCacheUriAmbiguity  （ 包含所有 ）
 *              bundle-1    = OCacheUriAmbiguity  （ 当前 Osgi 相关 ）
 *              bundle-2    = OCacheUriAmbiguity
 *              ......
 * </code></pre>
 *
 * @author lang : 2024-04-21
 */
class OCacheUriAmbiguity extends AbstractAmbiguity implements OCacheUri {

    private final ConcurrentMap<HttpMethod, Set<String>> uri = new ConcurrentHashMap<>();

    OCacheUriAmbiguity(final Bundle bundle) {
        super(bundle);
        {
            this.uri.put(HttpMethod.GET, new HashSet<>());
            this.uri.put(HttpMethod.POST, new HashSet<>());
            this.uri.put(HttpMethod.PUT, new HashSet<>());
            this.uri.put(HttpMethod.DELETE, new HashSet<>());
        }
    }

    @Override
    public ConcurrentMap<HttpMethod, Set<String>> value() {
        return this.uri;
    }


    @Override
    public Set<String> valueUri(final HttpMethod method) {
        if (Objects.isNull(method)) {
            return this.uri.values().stream().reduce(new HashSet<>(), (merged, each) -> {
                merged.addAll(each);
                return merged;
            });
        } else {
            return this.uri.computeIfAbsent(method, key -> new HashSet<>());
        }
    }

    @Override
    public OCacheUri add(final String uri, final HttpMethod method) {
        if (Objects.isNull(method)) {
            // 方法为空的添加（全中）
            this.uri.keySet().forEach((httpMethod) -> this.add(uri, httpMethod));
        } else {
            // 方法不为空的添加
            final Set<String> uriSet = this.valueUri(method);
            uriSet.add(uri);
            this.uri.put(method, uriSet);
        }
        return this;
    }

    @Override
    public OCacheUri remove(final String uri, final HttpMethod method) {
        if (Objects.isNull(method)) {
            this.uri.keySet().forEach((httpMethod) -> this.remove(uri, httpMethod));
        } else {
            final Set<String> uriSet = this.valueUri(method);
            uriSet.remove(uri);
            this.uri.put(method, uriSet);
        }
        return this;
    }
}
