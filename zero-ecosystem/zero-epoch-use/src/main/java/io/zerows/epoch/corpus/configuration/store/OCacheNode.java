package io.zerows.epoch.corpus.configuration.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.configuration.atom.NodeNetwork;
import io.zerows.epoch.corpus.configuration.atom.NodeVertx;
import io.zerows.epoch.corpus.metadata.zdk.running.OCache;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-20
 */
public interface OCacheNode extends OCache<NodeVertx> {

    Cc<String, OCacheNode> CC_SKELETON = Cc.open();

    static OCacheNode of(final Bundle bundle) {
        return CC_SKELETON.pick(() -> new OCacheNodeAmbiguity(bundle),
            Ut.Bnd.keyCache(bundle, OCacheNodeAmbiguity.class));
    }

    static OCacheNode of() {
        return of(null);
    }

    NodeNetwork network();
}
