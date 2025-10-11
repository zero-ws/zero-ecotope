package io.zerows.cortex;

import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;

/**
 * 动态路由管理器工厂，用于提取动态路由管理器专用，同时兼容 OSGI 和非 OSGI 环境，实现类由 jet 项目提供
 *
 * @author lang : 2024-06-26
 */
public interface AxisDynamicFactory {

    Axis getAxis();

    boolean isEnabled(HBundle owner);
}
