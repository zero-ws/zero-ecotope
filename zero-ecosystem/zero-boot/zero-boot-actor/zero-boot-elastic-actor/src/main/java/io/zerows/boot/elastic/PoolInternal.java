package io.zerows.boot.elastic;

import io.zerows.platform.enums.typed.ChangeFlag;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-11-01
 */
interface PoolInternal {

    ConcurrentMap<ChangeFlag, ConcurrentHashMap<String, EsIndex>> POOL_INDEX = new ConcurrentHashMap<ChangeFlag, ConcurrentHashMap<String, EsIndex>>() {
        {
            this.put(ChangeFlag.ADD, new ConcurrentHashMap<>());
            this.put(ChangeFlag.UPDATE, new ConcurrentHashMap<>());
            this.put(ChangeFlag.DELETE, new ConcurrentHashMap<>());
        }
    };

    ConcurrentMap<ChangeFlag, Function<String, EsIndex>> POOL_INDEX_SUPPLIER = new ConcurrentHashMap<ChangeFlag, Function<String, EsIndex>>() {
        {
            this.put(ChangeFlag.ADD, EsAddIndexer::new);
            this.put(ChangeFlag.UPDATE, EsUpdateIndexer::new);
            this.put(ChangeFlag.DELETE, EsDeleteIndexer::new);
        }
    };
}
