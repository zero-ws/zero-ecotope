package io.zerows.epoch.corpus.model.running;

import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.corpus.model.Event;

import java.io.Serializable;
import java.util.Objects;

/**
 * 路由管理器，动态路由发布的关键，替换原始的 Uri 系列的存储，绑定了当前 Uri 系列的路由管理器，所以当前管理器只负责线程级的路由管理，针对
 * 当前线程中的 Router 执行相关的路由管理操作，唯一的不同点在于此处要将 {@link Route} 的原始路由保存下来，并且方便执行移除等相关操作，
 * 所以此处的 Router 只是一个单纯的引用。
 *
 * @author lang : 2024-05-04
 */
public class RunRoute implements Serializable {

    private final RunServer server;
    private final RunThread thread;
    private final Route route;
    private Event event;
    private String key;

    public RunRoute(final RunServer server) {
        this.server = server;
        this.thread = RunThread.one();                      // 线程相关信息

        final Router router = server.refRouter();
        // 构造新路由
        this.route = router.route();
    }

    public RunRoute refEvent(final Event event) {
        Objects.requireNonNull(event);
        this.event = event;
        this.key = event.getMethod().name() + VString.SLASH + event.getPath();
        return this;
    }

    public RunThread thread() {
        return this.thread;
    }

    public String key() {
        return this.key;
    }

    public Event refEvent() {
        return this.event;
    }

    public Router refRouter() {
        return this.server.refRouter();
    }

    public Route instance() {
        return this.route;
    }
}
