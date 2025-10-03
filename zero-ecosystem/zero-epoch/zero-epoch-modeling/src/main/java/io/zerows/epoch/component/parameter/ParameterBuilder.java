package io.zerows.epoch.component.parameter;

import io.vertx.ext.web.RoutingContext;
import io.zerows.metadata.program.KRef;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.exception.web._60050Exception501NotSupport;
import io.zerows.epoch.program.Ut;

/**
 * 参数构造器，用于构造各种参数对应的相关对象，直接为上层调用提供依据，替换原来的 ToWeb
 *
 * @author lang : 2024-04-21
 */
public interface ParameterBuilder<SOURCE> {

    static ParameterBuilder<RoutingContext> ofAgent() {
        return ParameterAgent.of();
    }

    static ParameterBuilder<Envelop> ofWorker() {
        return ParameterWorker.of();
    }

    default Object build(final SOURCE envelop, final Class<?> type) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    default Object build(final SOURCE envelop, final Class<?> type, final KRef underway) {
        throw new _60050Exception501NotSupport(this.getClass());
    }
}

interface T {
    static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || Ut.isImplement(paramType, expected);
    }
}