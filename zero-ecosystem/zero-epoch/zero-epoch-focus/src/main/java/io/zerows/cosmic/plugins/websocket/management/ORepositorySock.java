package io.zerows.cosmic.plugins.websocket.management;

import io.zerows.cosmic.plugins.websocket.Remind;
import io.zerows.cosmic.plugins.websocket.SockInquirer;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.management.ORepository;
import io.zerows.epoch.management.AbstractAmbiguity;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Set;

/**
 * @author lang : 2024-04-21
 */
public class ORepositorySock extends AbstractAmbiguity implements ORepository {


    protected ORepositorySock(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public void whenStart(final HSetting setting) {

        final OCacheSock processor = OCacheSock.of(this.caller());
        final Inquirer<Set<Remind>> scanner = Ut.singleton(SockInquirer.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }
}
