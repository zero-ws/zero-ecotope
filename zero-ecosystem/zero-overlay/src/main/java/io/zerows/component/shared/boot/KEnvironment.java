package io.zerows.component.shared.boot;

import io.zerows.component.log.LogAs;
import io.zerows.constant.VBoot;
import io.zerows.constant.VMessage;
import io.zerows.enums.Environment;
import io.zerows.enums.app.OsType;
import io.zerows.runtime.HMacrocosm;
import io.zerows.support.UtBase;

import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * 「内置环境变量准备」
 * 开发环境综述，用于处理开发环境专用的启动器，在 KLauncher 中会被调用，主要检查
 * .dev.development 文件，初始化当前环境的系统环境变量，执行完成后处理下一步骤
 * 关于 {@link Environment} 的计算流程如下
 * <pre><code>
 *     1. 默认情况下，如果检测到 .dev.development 文件应该是
 *        {@link Environment#Development}
 *     2. 但为了在开发环境中可直接使用生产环境 / 模拟环境相关环境变量，会在文件存在时检查 `ZERO_ENV` 的值，若不存在该值则不考虑环境变量的切换，若存在此环境变量则直接将环境变量强制转换成对应的值
 *     3. 生产环境中不会将 .dev.development 打包，且不会命中此文件开启开发模式
 *
 * </code></pre>
 *
 * @author lang : 2023-05-30
 */
public class KEnvironment {
    /**
     * 环境变量初始化验证，内置启动会被两处调用
     * <pre><code>
     *     1. Ok 组件（Mock模拟环境）
     *     2. 标准初始化 CloudPre 组件会调用环境变量初始化
     * </code></pre>
     * 环境会包含两种初始化
     * <pre><code>
     *     1. {@link Environment#Development} 开发环境
     *        只有开发环境会执行 .env.development 环境变量的初始化流程，其他环境不会执行初始化。
     *     2. {@link Environment#Production} 生产环境
     *        生产环境在执行过程中会提前初始化环境相关信息。
     * </code></pre>
     */
    public static void initialize() {
        /*
         * 判断是否开启了开发环境，如果开启了开发环境，那么就会读取 .dev.development 文件
         * 加载文件中的环境变量到系统层（只适用于开发）
         */
        if (UtBase.ioExist(VBoot._ENV_DEVELOPMENT)) {
            // 1. 环境变量设置
            final OsType os = UtBase.envOs();
            LogAs.Boot.warn(KEnvironment.class, VMessage.KEnvironment.DEVELOPMENT,
                os.name(), VBoot._ENV_DEVELOPMENT);
            final Properties properties = UtBase.ioProperties(VBoot._ENV_DEVELOPMENT);
            // 1.1. 环境变量注入
            if (!properties.containsKey(HMacrocosm.ZERO_ENV)) {
                properties.put(HMacrocosm.ZERO_ENV, Environment.Development.name());
            }
            /*
             * 开发环境需要带上启动参数，否则会报错，这里是为了解决 JDK 9 以上版本的问题
             * --add-opens java.base/java.util=ALL-UNNAMED
             * --add-opens java.base/java.lang=ALL-UNNAMED
             */
            final ConcurrentMap<String, String> written = UtBase.envOut(properties);

            // 2. 环境变量打印
            final String environments = UtBase.envString(written);
            LogAs.Boot.info(KEnvironment.class, VMessage.KEnvironment.ENV, environments);
        }
    }
}
