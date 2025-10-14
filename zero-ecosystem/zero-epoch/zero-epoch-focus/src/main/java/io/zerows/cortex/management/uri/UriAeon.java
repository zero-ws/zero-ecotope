package io.zerows.cortex.management.uri;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 *
 * The soul of zero framework to get critical data / extract data
 */
public class UriAeon {
    /*
     * Thread -> Uri storage for dynamic routing deployment
     */
    private static final Cc<String, UriNeuro> CC_NEURO = Cc.openThread();
    private static final UriStore STORE = UriStore.create();

    /*
     * Registry route to ZeroAeon
     */
    public static void connect(final Router router) {
        /*
         * Initialize the routing system to get reference
         */
        final String threadId = Thread.currentThread().getName();
        CC_NEURO.pick(() -> UriNeuro.getInstance(threadId).bind(router));
        // FnZero.po?lThread(NEURO, () -> UriNeuro.getInstance(threadId).bind(router));
    }


    /*
     * Routing mounting here ( Dynamic Modification )
     */
    public static void mountRoute(final JsonObject config) {
        /*
         * 为原始 Route 对象构造新路由，此处每个线程中的 value 都会执行此操作
         */
        CC_NEURO.values().forEach(neuro -> neuro.addRoute(config));
    }

    /*
     * ADD / SEARCH / GET
     */
    // ADD
    public static void uriAdd(final UriMeta meta) {
        if (Objects.nonNull(meta)) {
            STORE.add(meta);
        }
    }

    // SEARCH
    public static List<UriMeta> uriSearch(final String keyword) {
        return STORE.search(keyword);
    }

    // GET
    public static List<UriMeta> uriGet() {
        return STORE.getAll();
    }
}