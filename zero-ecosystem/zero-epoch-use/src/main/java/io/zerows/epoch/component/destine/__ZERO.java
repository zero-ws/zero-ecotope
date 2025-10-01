package io.zerows.epoch.component.destine;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.annotations.monitor.Memory;


@SuppressWarnings("all")
interface POOL {
    @Memory(Hymn.class)
    Cc<String, Hymn> CCT_HYMN = Cc.openThread();

    @Memory(Conflate.class)
    Cc<String, Conflate> CCT_CONFLATE = Cc.openThread();
}