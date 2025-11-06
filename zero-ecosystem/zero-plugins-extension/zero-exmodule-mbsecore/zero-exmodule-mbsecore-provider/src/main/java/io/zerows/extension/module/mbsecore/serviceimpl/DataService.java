package io.zerows.extension.module.mbsecore.serviceimpl;

import io.zerows.extension.module.mbsecore.boot.AoCache;
import io.zerows.extension.module.mbsecore.servicespec.DataStub;

public class DataService implements DataStub {
    @Override
    public Boolean cleanDataAtom(final String key) {
        AoCache.CC_MODEL.remove(key);
        // AoCache.POOL_ATOM.remove(key);
        return Boolean.TRUE;
    }
}
