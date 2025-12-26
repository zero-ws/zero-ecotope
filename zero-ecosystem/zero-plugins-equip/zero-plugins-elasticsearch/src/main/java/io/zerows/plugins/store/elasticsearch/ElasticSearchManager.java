package io.zerows.plugins.store.elasticsearch;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

class ElasticSearchManager extends AddOnManager<ElasticSearchClient> {

    private static final Cc<String, ElasticSearchClient> CC_STORED = Cc.open();

    private static final ElasticSearchManager INSTANCE = new ElasticSearchManager();

    private ElasticSearchManager() {
    }

    @Override
    protected Cc<String, ElasticSearchClient> stored() {
        return CC_STORED;
    }

    static ElasticSearchManager of() {
        return INSTANCE;
    }
}