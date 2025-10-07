package io.zerows.epoch.spi.provider;

import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.spi.modeler.AtomNs;
import io.zerows.support.Ut;
import io.zerows.support.base.UtBase;

/**
 * 🔄 Zero框架内部专用名空间，也是默认名空间，默认名空间使用：
 * <pre><code>
 *     1. 📁 无应用名称的名空间：
 *        io.zerows.momo
 *     2. 🏷️ 带应用名称的名空间：
 *        io.zerows.{app}
 * </code></pre>
 * 🎯 若您使用了动态建模，那么最终模型标识符对应的名空间为：
 * <pre><code>
 *     [namespace] - [identifier]
 *     - 🔑 其中 namespace 为上边计算出来的值 {@link AtomNs#ns(String)}
 *     - 🏷️ 而 identifier 为方法 {@link AtomNs#ns(String, String)} 的第二个参数
 * </code></pre>
 * 🛠️ 若想有自己的名空间，则应该修改自己项目底层的 {@link AtomNs} 实现，并配置到 SPI 中
 * ⚠️ 注意上层环境变量中若存在 `Z_NS` 那么会直接使用它替换默认的名空间以适配不同的名空间
 * 相关信息。
 *
 * @author lang : 2023-05-08
 */
public class AtomNsZero implements AtomNs {
    private static final String NS_APP = "io.zerows.app.{0}";
    private static final String NS_DEFAULT = "io.zerows.r2mo";

    /**
     * 📋 此方法需要针对命名空间的信息加以特殊说明，新版多出了 Nacos 配置中心或其他第二选择的配置中心，于是有了多个名空间的做法
     * <pre>
     *     1. 🧩 AtomNs -> 主要针对 动态建模 和 静态建模 的名空间划分，这种名空间在不针对模型管理的场景之下可直接忽略
     *     2. 🔄 此接口中的名空间只通过如下方式进行配置
     *        {@link EnvironmentVariable#Z_NS} - Z_NS 环境变量
     *        单点应用中对此名空间没有任何需求
     *     3. ☁️ Nacos 中会包含两种命名空间
     *        - {@link EnvironmentVariable#R2MO_NS_CLOUD} 🌐 云端平台名空间
     *        - {@link EnvironmentVariable#R2MO_NS_APP} 🏢 应用专用名空间
     *        这两个名空间不在代码中出现，只会出现在 vertx-boot.yml 的配置文件中执行解析和替换
     *     4. ✅ 上述三种名空间互不影响
     * </pre>
     *
     * @param appName 🏷️ 应用名称
     *
     * @return 📁 名空间信息
     */
    @Override
    public String ns(final String appName) {
        // 🔧 先计算名空间前缀
        final String prefix = ENV.of().get(EnvironmentVariable.Z_NS, NS_APP);
        // 🔁 再计算名空间
        final String namespace = Ut.isNil(appName) ? NS_DEFAULT : UtBase.fromMessage(prefix, appName);
        return namespace;
    }
}