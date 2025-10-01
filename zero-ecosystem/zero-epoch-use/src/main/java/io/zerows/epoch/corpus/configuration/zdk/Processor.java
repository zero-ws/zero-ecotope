package io.zerows.epoch.corpus.configuration.zdk;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

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
