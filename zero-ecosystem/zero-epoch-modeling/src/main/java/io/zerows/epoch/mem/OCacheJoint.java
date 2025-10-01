package io.zerows.epoch.mem;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.action.OJointAction;
import io.zerows.epoch.corpus.model.action.OJointMap;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.running.OCache;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheJoint extends OCache<OJointAction> {
    Cc<String, OCacheJoint> CC_SKELETON = Cc.open();

    static OCacheJoint of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheJointAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheJointAmbiguity(bundle), cacheKey);
    }

    static OCacheJoint of() {
        return of(null);
    }

    static OJointMap entireJoint() {
        final OJointMap jointMap = new OJointMap();
        CC_SKELETON.get().values().forEach(each -> {
            final OJointMap eachMap = each.joint();
            jointMap.add(eachMap);
        });
        return jointMap;
    }

    OJointMap joint();
}
