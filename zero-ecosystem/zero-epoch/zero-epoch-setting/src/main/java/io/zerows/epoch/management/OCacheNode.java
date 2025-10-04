package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.NodeNetwork;
import io.zerows.epoch.basicore.NodeVertx;
import io.zerows.sdk.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-04-20
 */
public interface OCacheNode extends OCache<NodeVertx> {

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
