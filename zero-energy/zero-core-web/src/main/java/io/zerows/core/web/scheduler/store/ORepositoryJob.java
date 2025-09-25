package io.zerows.core.web.scheduler.store;

import io.zerows.core.util.Ut;
import io.zerows.core.web.scheduler.atom.Mission;
import io.zerows.module.metadata.store.OCacheClass;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import io.zerows.module.metadata.zdk.running.ORepository;
import io.zerows.module.metadata.zdk.uca.Inquirer;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author lang : 2024-04-19
 */
public class ORepositoryJob extends AbstractAmbiguity implements ORepository {

    protected ORepositoryJob(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public void whenStart(final HSetting setting) {

        final OCacheJob processor = OCacheJob.of(this.caller());
        final Inquirer<Set<Mission>> scanner = Ut.singleton(JobInquirer.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }
}
