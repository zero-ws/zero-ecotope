package io.zerows.extension.runtime.crud.uca.destine;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;
import io.zerows.ams.constant.em.modeling.EmModel;

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