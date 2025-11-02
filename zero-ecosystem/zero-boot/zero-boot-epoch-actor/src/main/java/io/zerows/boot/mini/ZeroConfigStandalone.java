package io.zerows.boot.mini;

import io.zerows.epoch.configuration.ConfigNorm;
import io.zerows.specification.configuration.HLauncher;

/**
 * 标准容器中的 {@link HLauncher.Pre} 处理器
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
@Deprecated
public class ZeroConfigStandalone extends ConfigNorm {
}
