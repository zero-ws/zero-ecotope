package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.JointAction;
import io.zerows.epoch.basicore.JointMap;
import io.zerows.sdk.management.OCache;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheJoint extends OCache<JointAction> {
    Cc<String, OCacheJoint> CC_SKELETON = Cc.open();

    static OCacheJoint of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheJointAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheJointAmbiguity(bundle), cacheKey);
    }

    static OCacheJoint of() {
        return of(null);
    }

    static JointMap entireJoint() {
        final JointMap jointMap = new JointMap();
        CC_SKELETON.get().values().forEach(each -> {
            final JointMap eachMap = each.joint();
            jointMap.add(eachMap);
        });
        return jointMap;
    }

    JointMap joint();
}
