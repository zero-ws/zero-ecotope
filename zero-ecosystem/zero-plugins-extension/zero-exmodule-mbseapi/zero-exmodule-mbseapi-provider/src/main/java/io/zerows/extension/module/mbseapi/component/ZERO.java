package io.zerows.extension.module.mbseapi.component;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.module.mbseapi.common.em.ParamMode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

interface Pool {

    Cc<String, JtIngest> CC_INGEST_INTERNAL = Cc.openThread();

    ConcurrentMap<ParamMode, Supplier<JtIngest>> INNER_INGEST = new ConcurrentHashMap<>() {
        {
            this.put(ParamMode.QUERY, () -> CC_INGEST_INTERNAL.pick(JtIngestQuery::new, JtIngestQuery.class.getName()));
            this.put(ParamMode.BODY, () -> CC_INGEST_INTERNAL.pick(JtIngestBody::new, JtIngestBody.class.getName()));
            this.put(ParamMode.DEFINE, () -> CC_INGEST_INTERNAL.pick(JtIngestDefine::new, JtIngestDefine.class.getName()));
            this.put(ParamMode.PATH, () -> CC_INGEST_INTERNAL.pick(JtIngestPath::new, JtIngestPath.class.getName()));
            this.put(ParamMode.FILE, () -> CC_INGEST_INTERNAL.pick(JtIngestFile::new, JtIngestFile.class.getName()));
        }
    };
}
