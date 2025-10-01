package io.zerows.epoch.corpus.web.websocket.store;

import io.zerows.epoch.corpus.web.websocket.atom.Remind;
import io.zerows.epoch.mem.OCacheClass;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.AbstractAmbiguity;
import io.zerows.epoch.sdk.metadata.running.ORepository;
import io.zerows.epoch.sdk.metadata.uca.Inquirer;
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
