package io.zerows.extension.runtime.ambient.uca.darkly;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityRule;
import io.zerows.extension.runtime.ambient.eon.em.TubeType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


interface Pool {
    ConcurrentMap<TubeType, Class<?>> TUBE_CLS = new ConcurrentHashMap<>() {
        {
            // type = ATOM
            this.put(TubeType.ATOM, TubeAtom.class);
            // type = PHASE
            this.put(TubeType.PHASE, TubePhase.class);
            // type = EXPRESSION
            this.put(TubeType.EXPRESSION, TubeExpression.class);
            // type = APPROVAL
            this.put(TubeType.APPROVAL, TubeApprove.class);
            // type = ATTACHMENT
            this.put(TubeType.ATTACHMENT, TubeAttachment.class);
        }
    };
}

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Tube {

    Cc<String, Tube> CC_TUBE = Cc.openThread();

    static Tube instance(final TubeType type) {
        if (Objects.isNull(type)) {
            // Empty Tube
            return CC_TUBE.pick(TubeEmpty::new, TubeEmpty.class.getName());
        }
        // Supplier for Tube
        final Class<?> instanceCls = Pool.TUBE_CLS.get(type);
        if (Objects.isNull(instanceCls)) {
            // Empty
            throw new _60050Exception501NotSupport(Tube.class);
        }
        return CC_TUBE.pick(() -> Ut.instance(instanceCls), instanceCls.getName());
    }

    Future<JsonObject> traceAsync(JsonObject data, XActivityRule rule);
}
