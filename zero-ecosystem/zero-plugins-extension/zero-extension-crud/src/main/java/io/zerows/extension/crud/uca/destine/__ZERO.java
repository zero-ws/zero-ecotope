package io.zerows.extension.crud.uca.destine;

import io.r2mo.typed.cc.Cc;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.enums.modeling.EmModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2023-08-18
 */
interface POOL {

    @Memory(Probe.class)
    Cc<String, Probe> CCT_PROBE = Cc.openThread();

    ConcurrentMap<EmModel.Join, Supplier<Probe>> PROBE_MAP = new ConcurrentHashMap<>() {
        {
            this.put(EmModel.Join.CRUD, ProbeCrud::new);
        }
    };
}