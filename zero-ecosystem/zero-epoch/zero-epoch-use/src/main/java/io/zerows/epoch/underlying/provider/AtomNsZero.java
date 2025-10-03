package io.zerows.epoch.underlying.provider;

import io.zerows.epoch.corpus.cloud.LogCloud;
import io.zerows.epoch.program.Ut;
import io.zerows.platform.HEnvironmentVariable;
import io.zerows.platform.constant.VEnv;
import io.zerows.spi.modeler.AtomNs;
import io.zerows.support.UtBase;

/**
 * Zero框架内部专用名空间，也是默认名空间，默认名空间使用：
 * <pre><code>
 *     1. 无应用名称的名空间：
 *        io.mature.aeon
 *     2. 带应用名称的名空间：
 *        io.mature.{app}
 * </code></pre>
 * 若您使用了动态建模，那么最终模型标识符对应的名空间为：
 * <pre><code>
 *     [namespace] - [identifier]
 *     - 其中 namespace 为上边计算出来的值 {@link AtomNs#ns(String)}
 *     - 而 identifier 为方法 {@link AtomNs#ns(String, String)} 的第二个参数
 * </code></pre>
 * 若想有自己的名空间，则应该修改自己项目底层的 {@link AtomNs} 实现，并配置到 SPI 中
 * 注意上层环境变量中若存在 `Z_NS` 那么会直接使用它替换默认的名空间以适配不同的名空间
 * 相关信息。
 *
 * @author lang : 2023-05-08
 */
public class AtomNsZero implements AtomNs {
    @Override
    public String ns(final String appName) {
        // 先计算名空间前缀
        final String prefix = Ut.envWith(HEnvironmentVariable.Z_NS, VEnv.APP.NS);
        // 再计算名空间
        final String namespace = Ut.isNil(appName)
            ? VEnv.APP.NS_DEFAULT :                     // io.mature.aeon
            UtBase.fromMessage(prefix, appName);           // io.mature.{0} 或 Z_NS
        LogCloud.LOG.Aeon.debug(this.getClass(),
            "名空间: {0}, 前缀：{1}", namespace, prefix);
        LogCloud.LOG.Aeon.debug(this.getClass(),
            "应用名：{0}", appName);
        return namespace;
    }
}
