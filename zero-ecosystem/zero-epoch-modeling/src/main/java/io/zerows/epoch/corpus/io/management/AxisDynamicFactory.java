package io.zerows.epoch.corpus.io.management;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;
import org.osgi.framework.Bundle;

/**
 * 动态路由管理器工厂，用于提取动态路由管理器专用，同时兼容 OSGI 和非 OSGI 环境，实现类由 jet 项目提供
 *
 * @author lang : 2024-06-26
 */
public interface AxisDynamicFactory {

    OAxis getAxis();

    boolean isEnabled(Bundle owner);

    default OLog logger() {
        return Ut.Log.plugin(this.getClass());
    }
}
