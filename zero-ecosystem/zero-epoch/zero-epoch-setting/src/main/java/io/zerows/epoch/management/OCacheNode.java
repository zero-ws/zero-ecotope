package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertxLegacy;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-04-20
 */
@Deprecated
public interface OCacheNode extends OCache<NodeVertxLegacy> {

    Cc<String, OCacheNode> CC_SKELETON = Cc.open();

    static OCacheNode of(final HBundle bundle) {
        return CC_SKELETON.pick(() -> new OCacheNodeAmbiguity(bundle),
            HBundle.id(bundle, OCacheNodeAmbiguity.class));
    }

    static OCacheNode of() {
        return of(null);
    }

    NodeNetwork network();
}
