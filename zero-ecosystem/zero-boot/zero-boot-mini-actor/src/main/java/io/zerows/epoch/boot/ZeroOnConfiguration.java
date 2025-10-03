package io.zerows.epoch.boot;

import io.zerows.metadata.app.KConfig;
import io.zerows.specification.configuration.boot.HMature;

/**
 * 标准容器中的 {@link HMature.HPre} 处理器
 * 针对框架中的插件执行双重处理：
 * <pre><code>
 *     Vertx容器已执行完实例化
 *     - 容器启动之前的内置插件部分
 *     Vertx Extension / Ambient 容器已执行完实例化
 *     - 容器扩展部分加载完成之后的插件部分
 * </code></pre>
 *
 * @author lang : 2023-05-31
 */
public class ZeroOnConfiguration extends KConfig {
}
