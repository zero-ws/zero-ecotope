package io.zerows.epoch.management;

import io.zerows.epoch.basicore.JointAction;
import io.zerows.epoch.basicore.JointMap;
import io.zerows.sdk.management.AbstractAmbiguity;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
class OCacheJointAmbiguity extends AbstractAmbiguity implements OCacheJoint {

    final JointMap joint = new JointMap();

    OCacheJointAmbiguity(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public JointMap joint() {
        return this.joint;
    }

    @Override
    public OCacheJoint add(final JointAction action) {
        this.joint.add(action);
        return this;
    }

    @Override
    public OCacheJoint remove(final JointAction action) {
        this.joint.remove(action);
        return this;
    }
}
