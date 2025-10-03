package io.zerows.epoch.sdk.environment;

import io.zerows.component.log.OLog;
import io.zerows.support.Ut;

/**
 * Builder 模式下的执行器，用来执行和处理特定对象专用
 *
 * @author lang : 2024-04-20
 */
public interface Processor<T, C> {

    void makeup(T target, C setting);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}
