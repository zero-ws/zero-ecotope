package io.zerows.extension.mbse.action.uca.param;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.mbse.action.eon.em.ParamMode;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

interface Pool {

    Cc<String, JtIngest> CC_INGEST_INTERNAL = Cc.openThread();

    ConcurrentMap<ParamMode, Supplier<JtIngest>> INNER_INGEST = new ConcurrentHashMap<>() {
        {
            this.put(ParamMode.QUERY, () -> CC_INGEST_INTERNAL.pick(QueryIngest::new, QueryIngest.class.getName()));
            this.put(ParamMode.BODY, () -> CC_INGEST_INTERNAL.pick(BodyIngest::new, BodyIngest.class.getName()));
            this.put(ParamMode.DEFINE, () -> CC_INGEST_INTERNAL.pick(DefineIngest::new, DefineIngest.class.getName()));
            this.put(ParamMode.PATH, () -> CC_INGEST_INTERNAL.pick(PathIngest::new, PathIngest.class.getName()));
            this.put(ParamMode.FILE, () -> CC_INGEST_INTERNAL.pick(FileIngest::new, FileIngest.class.getName()));
        }
    };
}
