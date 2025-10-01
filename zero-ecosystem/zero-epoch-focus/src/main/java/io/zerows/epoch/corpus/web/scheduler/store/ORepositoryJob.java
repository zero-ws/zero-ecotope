package io.zerows.epoch.corpus.web.scheduler.store;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.metadata.store.OCacheClass;
import io.zerows.epoch.corpus.metadata.zdk.AbstractAmbiguity;
import io.zerows.epoch.corpus.metadata.zdk.running.ORepository;
import io.zerows.epoch.corpus.metadata.zdk.uca.Inquirer;
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
