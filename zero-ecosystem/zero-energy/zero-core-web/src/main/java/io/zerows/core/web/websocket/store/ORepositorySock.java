package io.zerows.core.web.websocket.store;

import io.zerows.core.util.Ut;
import io.zerows.core.web.websocket.atom.Remind;
import io.zerows.module.metadata.store.OCacheClass;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import io.zerows.module.metadata.zdk.running.ORepository;
import io.zerows.module.metadata.zdk.uca.Inquirer;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
public class ORepositorySock extends AbstractAmbiguity implements ORepository {


    protected ORepositorySock(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public void whenStart(final HSetting setting) {

        final OCacheSock processor = OCacheSock.of(this.caller());
        final Inquirer<Set<Remind>> scanner = Ut.singleton(SockInquirer.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }
}
