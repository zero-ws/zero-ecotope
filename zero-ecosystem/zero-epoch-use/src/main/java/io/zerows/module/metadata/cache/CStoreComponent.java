package io.zerows.module.metadata.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.annotations.monitor.Memory;
import io.zerows.specification.atomic.HCombiner;
import io.zerows.specification.atomic.HCommand;

/**
 * @author lang : 2024-04-17
 */
interface CStoreComponent {


    /*
     * 「界面级别处理」
     */
    @SuppressWarnings("all")
    @Memory(HCombiner.class)
    Cc<String, HCombiner> CC_COMBINER = Cc.openThread();


    /*
     * 「线程级」
     * CCT_EVENT: Aeon中的所有Event集合
     */
    @SuppressWarnings("all")
    @Memory(HCommand.Async.class)
    Cc<String, HCommand.Async> CCT_EVENT = Cc.openThread();
}
