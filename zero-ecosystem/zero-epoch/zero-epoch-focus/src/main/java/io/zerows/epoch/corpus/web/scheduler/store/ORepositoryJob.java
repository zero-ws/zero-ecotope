package io.zerows.epoch.corpus.web.scheduler.store;

import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.management.ORepository;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Set;

/**
 * @author lang : 2024-04-19
 */
public class ORepositoryJob extends AbstractAmbiguity implements ORepository {

    protected ORepositoryJob(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public void whenStart(final HSetting setting) {

        final OCacheJob processor = OCacheJob.of(this.caller());
        final Inquirer<Set<Mission>> scanner = Ut.singleton(JobInquirer.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }
}
