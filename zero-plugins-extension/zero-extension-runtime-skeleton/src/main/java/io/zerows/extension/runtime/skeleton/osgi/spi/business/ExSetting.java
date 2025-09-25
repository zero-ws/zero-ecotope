package io.zerows.extension.runtime.skeleton.osgi.spi.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 个人设置通道
 *
 * @author lang : 2024-04-09
 */
public interface ExSetting {
    /**
     * 读取个人设置信息，数据结构根据 MY_NOTIFY 之后的配置而定
     * <pre><code>
     *     {
     *         "notify": "提醒设置"
     *     }
     * </code></pre>
     *
     * @param user   用户key
     * @param dimKey 维度key
     *
     * @return 读取结果
     */
    Future<JsonObject> settingAsync(String user, String dimKey);
}
