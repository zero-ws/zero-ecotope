package io.zerows.cortex;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;

/**
 * 路由管理工厂，使用 SPID 执行扩展
 * <pre>
 *     - 1. 直接加载 SPI 提取 AxisFactory 的实现
 *     - 2. 调用 AxisFactory 实现中的方法提取路由管理器
 * </pre>
 * 根据扩展的方式来提取不同的路由管理器
 */
public interface AxisFactory {
    Cc<String, AxisFactory> CC_FACTORY = Cc.openThread();

    static AxisFactory of(final String spid) {
        return CC_FACTORY.pick(() -> HPI.findOne(AxisFactory.class, spid), spid);
    }

    Axis getAxis();

    boolean isEnabled(HBundle owner);
}
