package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.configuration.HSetting;

/**
 *
 * @author lang : 2025-10-06
 */
class ZeroPowerBridge implements ZeroPower {
    private static final Cc<String, ZeroPower.Source> CC_SOURCE = Cc.openThread();

    @Override
    public HSetting compile() {
        final ZeroPower.Source source = CC_SOURCE.pick(ZeroSource::new, ZeroSource.class.getName());

        final YmConfiguration configuration = source.load();
        return null;
    }
}
