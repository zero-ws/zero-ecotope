package io.zerows.extension.mbse.basement.agent.service;

import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.eon.AoCache;

import java.util.concurrent.ConcurrentMap;

public class DataService implements DataStub {
    @Override
    public Boolean cleanDataAtom(final String key) {
        final ConcurrentMap<String, Model> cdModel = AoCache.CC_MODEL.get();
        cdModel.remove(key);
        // AoCache.POOL_ATOM.remove(key);
        return Boolean.TRUE;
    }
}
