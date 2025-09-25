package io.zerows.core.web.model.store;

import io.zerows.core.web.model.atom.action.OJointAction;
import io.zerows.core.web.model.atom.action.OJointMap;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-21
 */
class OCacheJointAmbiguity extends AbstractAmbiguity implements OCacheJoint {

    final OJointMap joint = new OJointMap();

    OCacheJointAmbiguity(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public OJointMap joint() {
        return this.joint;
    }

    @Override
    public OCacheJoint add(final OJointAction action) {
        this.joint.add(action);
        return this;
    }

    @Override
    public OCacheJoint remove(final OJointAction action) {
        this.joint.remove(action);
        return this;
    }
}
