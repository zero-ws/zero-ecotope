package io.zerows.extension.crud.uca.dao;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.annotations.meta.Memory;


@SuppressWarnings("all")
interface POOL {

    @Memory(Operate.class)
    Cc<String, Operate> CCT_OPERATE = Cc.openThread();
}
