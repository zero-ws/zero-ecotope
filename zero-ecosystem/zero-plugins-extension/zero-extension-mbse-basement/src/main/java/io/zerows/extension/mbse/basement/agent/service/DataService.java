package io.zerows.extension.mbse.basement.agent.service;

import io.zerows.extension.mbse.basement.eon.AoCache;

public class DataService implements DataStub {
    @Override
    public Boolean cleanDataAtom(final String key) {
        AoCache.CC_MODEL.remove(key);
        // AoCache.POOL_ATOM.remove(key);
        return Boolean.TRUE;
    }
}
