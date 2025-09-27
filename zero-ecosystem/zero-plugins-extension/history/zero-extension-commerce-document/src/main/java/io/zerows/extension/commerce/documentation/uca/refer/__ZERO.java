package io.zerows.extension.commerce.documentation.uca.refer;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.annotations.Memory;
import io.zerows.extension.commerce.documentation.eon.em.EmRefer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2023-09-25
 */
interface CACHE {

    @Memory(Quote.class)
    Cc<String, Quote> CCT_QUOTE = Cc.openThread();
}

interface POOL {

    ConcurrentMap<EmRefer.Entity, Supplier<Quote>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(EmRefer.Entity.DOC, QuoteDoc::new);
        }
    };
}
