package io.zerows.epoch.metadata.environment;

import io.zerows.platform.enums.EmService;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.configuration.HStation;
import io.zerows.spi.HEquip;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * 入口配置专用程序，内置返回 {@link HConfig} 的对应配置信息，可根据提供的配置 key 值直接处理相关配置，内部屏蔽单机内容
 * <pre><code>
 *     1. （OSGI）{@link HSetting} 此配置信息为核心主配置，不对外，但可以从接口返回，防止：配置和配置交叉
 *     2. {@link HStation} ( Not for OSGI )
 *     3. （OSGI）{@link HEquip} 此为初始化配置，已经在 OSGI 和非 OSGI 之间实现了配置提取，用来构建设置专用
 *     4. {@link HEnergy} 和 {@link HBoot} 只有在自定义启动器的时候会使用。
 * </code></pre>
 * 配置提取流程，分两种模式：Platform / App 两种
 * <pre><code>
 *     1. Platform
 *        平台模式先读取 conf 中单机应用程序专用的全套配置信息（包含 Infix 和 Extension 对应配置）
 *        若不存在 conf 中提供的内容，则读取 Bundle 内置配置
 *     2. App
 *        应用模式，直接读取 apps/conf 之下的配置信息，结构和单机配置结构一致，但应用会有一个主配置说明
 * </code></pre>
 * 文件系统中的配置为恢复默认专用配置，和 zero-battery 结合到一起之后，所有配置参数会暂存到数据库中（特别是模块配置参数），当前对象
 * BundleInternalConfig 会提供服务的上下文环境，服务上下文环境会提供相关配置信息，结合服务参数最终实现对应的参数相关信息。
 *
 * @author lang : 2024-07-01
 */
public class ContextOfApp extends ContextOfModule {
    private final HSetting setting;

    public ContextOfApp(final Bundle owner) {
        super(owner);
        // final HEquip equip = OZeroEquip.of(owner);
        this.setting = null; // equip.initialize();
    }

    @Override
    public HSetting setting() {
        // 延迟处理模式
        if (Objects.isNull(this.owner())) {
            this.logger().warn("This method could not accept null `caller` parameter. Setting will be null");
            // 非 OSGI 环境，无法调用
            return null;
        }
        return this.setting;
    }

    @Override
    public EmService.Context type() {
        return EmService.Context.APP;
    }

    @Override
    public Class<?> signalClass() {
        return IsReady.class;
    }

    public static class IsReady implements OK {

    }
}
