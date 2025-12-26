package io.zerows.plugins.store.elasticsearch;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

public class ElasticSearchProvider extends AddOnProvider<ElasticSearchClient> {
    protected ElasticSearchProvider(AddOn<ElasticSearchClient> addOn) {
        super(addOn);
    }
}