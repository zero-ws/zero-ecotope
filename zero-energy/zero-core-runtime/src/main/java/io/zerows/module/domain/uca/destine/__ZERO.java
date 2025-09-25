package io.zerows.module.domain.uca.destine;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;


@SuppressWarnings("all")
interface POOL {
    @Memory(Hymn.class)
    Cc<String, Hymn> CCT_HYMN = Cc.openThread();

    @Memory(Conflate.class)
    Cc<String, Conflate> CCT_CONFLATE = Cc.openThread();
}