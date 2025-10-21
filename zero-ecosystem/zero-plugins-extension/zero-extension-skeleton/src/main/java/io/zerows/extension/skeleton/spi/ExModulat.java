package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 模块化专用的核心配置接口，提取应用配置专用，此处配置主要为扩展配置，即存储在 B_BLOCK 和 B_BAG 中的核心配置，返回数据的配置结构如下
 * <pre><code>
 *     app-01 = data
 *     app-02 = data
 *     app-03 = data
 * </code></pre>
 * 此处的结构会直接挂载到 X_APP 的读取中，此处 extension 的响应数据结构如：
 * <pre><code>
 *     {
 *         "key": "id，应用程序ID",
 *         "mHotel": {
 *             "comment": "模块为 mHotel 的参数集"
 *         },
 *         "bags": [
 *             "子应用清单"
 *         ]
 *     }
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ExModulat {
    /**
     * 输入结构
     * <pre><code>
     *     {
     *         "key": "id"
     *     }
     * </code></pre>
     *
     * @param appJson 应用 X_APP 表结构
     *
     * @return 返回响应结构数据
     */
    default Future<JsonObject> extension(final JsonObject appJson) {
        return this.extension(appJson, false);
    }

    Future<JsonObject> extension(JsonObject appJson, boolean open);

    /**
     * 直接输入 id
     *
     * @param appId 应用ID
     *
     * @return 返回扩展配置数据
     */
    default Future<JsonObject> extension(final String appId) {
        return this.extension(appId, false);
    }

    Future<JsonObject> extension(String appId, boolean open);
}
