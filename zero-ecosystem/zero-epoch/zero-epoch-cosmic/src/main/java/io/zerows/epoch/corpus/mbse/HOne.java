package io.zerows.epoch.corpus.mbse;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.mbse.metadata.KModule;
import io.zerows.epoch.database.jooq.operation.UxJoin;
import io.zerows.epoch.database.jooq.operation.UxJooq;

import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public interface HOne<T> {
    Cc<String, HOne> CC_JOOQ = Cc.openThread();

    static HOne<UxJooq> jooq() {
        return CC_JOOQ.pick(HOneJooq::new, HOneJooq.class.getName());
    }

    static HOne<UxJoin> join() {
        return CC_JOOQ.pick(HOneJoin::new, HOneJoin.class.getName());
    }

    static HOne<ConcurrentMap<String, Class<?>>> type() {
        return CC_JOOQ.pick(HOneType::new, HOneType.class.getName());
    }

    /*
     * This method is for different combining operation between two
     * KModules
     * 1) The main KModule      module
     * 2) The joined KModule    connect
     */
    T combine(KModule module, KModule connect, MultiMap headers);

    default T combine(final KModule module, final MultiMap configuration) {
        return this.combine(module, null, configuration);
    }

    default T combine(final KModule module, final KModule connect) {
        return this.combine(module, connect, MultiMap.caseInsensitiveMultiMap());
    }

    interface Fn {
        static HOne<BiFunction<JsonObject, JsonObject, JsonObject>> hybrid() {
            return CC_JOOQ.pick(HOneHybrid::new, HOneHybrid.class.getName());
        }
    }
}
