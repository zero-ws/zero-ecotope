package io.zerows.extension.crud.uca;

import io.zerows.platform.enums.modeling.EmModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-11-05
 */
class ProbeTool {
    static final ConcurrentMap<EmModel.Join, Supplier<Probe>> PROBE_SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(EmModel.Join.CRUD, ProbeCrud::new);
        }
    };
}
