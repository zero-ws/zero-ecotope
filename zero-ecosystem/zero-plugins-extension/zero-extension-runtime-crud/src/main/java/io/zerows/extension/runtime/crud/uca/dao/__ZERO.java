package io.zerows.extension.runtime.crud.uca.dao;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.annotation.Memory;


@SuppressWarnings("all")
interface POOL {

    @Memory(Operate.class)
    Cc<String, Operate> CCT_OPERATE = Cc.openThread();
}
