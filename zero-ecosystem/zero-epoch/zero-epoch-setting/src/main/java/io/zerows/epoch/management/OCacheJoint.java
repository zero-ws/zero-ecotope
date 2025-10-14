package io.zerows.epoch.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.JointAction;
import io.zerows.epoch.basicore.JointMap;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-04-21
 */
public interface OCacheJoint extends OCache<JointAction> {
    Cc<String, OCacheJoint> CC_SKELETON = Cc.open();

    static OCacheJoint of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheJointAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheJointAmbiguity(bundle), cacheKey);
    }

    static OCacheJoint of() {
        return of(null);
    }

    static JointMap entireJoint() {
        final JointMap jointMap = new JointMap();
        CC_SKELETON.values().forEach(each -> {
            final JointMap eachMap = each.joint();
            jointMap.add(eachMap);
        });
        return jointMap;
    }

    JointMap joint();
}
