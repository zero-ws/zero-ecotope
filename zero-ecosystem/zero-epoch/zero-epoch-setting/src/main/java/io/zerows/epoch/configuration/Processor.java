package io.zerows.epoch.configuration;

import io.zerows.component.log.LogO;
import io.zerows.support.Ut;

/**
 * Builder 模式下的执行器，用来执行和处理特定对象专用
 *
 * @author lang : 2024-04-20
 */
public interface Processor<T, C> {

    void makeup(T target, C setting);

    default LogO logger() {
        return Ut.Log.configure(this.getClass());
    }
}
