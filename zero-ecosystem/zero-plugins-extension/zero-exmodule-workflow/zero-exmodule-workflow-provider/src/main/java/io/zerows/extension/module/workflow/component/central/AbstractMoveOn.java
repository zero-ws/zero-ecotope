package io.zerows.extension.module.workflow.component.central;

import io.zerows.extension.module.workflow.component.component.MoveOn;
import io.zerows.extension.module.workflow.component.toolkit.ULinkage;
import io.zerows.extension.module.workflow.component.toolkit.UTicket;
import io.zerows.extension.module.workflow.metadata.MetaInstance;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractMoveOn extends BehaviourStandard implements MoveOn {
    protected transient UTicket todoKit;
    protected transient ULinkage linkageKit;

    @Override
    public Behaviour bind(final MetaInstance metadata) {
        Objects.requireNonNull(metadata);
        this.todoKit = new UTicket(metadata);
        this.linkageKit = new ULinkage(metadata);
        return super.bind(metadata);
    }
}
