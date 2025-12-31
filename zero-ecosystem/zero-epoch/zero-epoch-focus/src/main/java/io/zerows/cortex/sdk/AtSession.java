package io.zerows.cortex.sdk;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.SessionStore;

/**
 * {@link SessionStore} 专用工厂接口，用于创建系统所需的会话存储实例
 *
 * @author lang : 2025-12-31
 */
public interface AtSession {

    Future<SessionStore> createStore(Vertx vertx, JsonObject options);
}
