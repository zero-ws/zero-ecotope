package io.zerows.epoch.corpus.web.security.store;

import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.management.OCacheClass;
import io.zerows.support.Ut;
import io.zerows.sdk.osgi.AbstractAmbiguity;
import io.zerows.sdk.management.ORepository;
import io.zerows.sdk.environment.Inquirer;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author lang : 2024-04-22
 */
public class ORepositorySecurity extends AbstractAmbiguity implements ORepository {

    public ORepositorySecurity(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public void whenStart(final HSetting setting) {
        this.whenStartInternal(setting);
    }

    @Override
    public void whenUpdate(final HSetting setting) {
        this.whenStartInternal(setting);
    }

    private void whenStartInternal(final HSetting setting) {

        final OCacheSecurity processor = OCacheSecurity.of(this.caller());
        final Inquirer<Set<Aegis>> scanner = Ut.singleton(WallInquirer.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }

    @Override
    public void whenRemove() {
        final OCacheSecurity processor = OCacheSecurity.of(this.caller());

        final Set<Aegis> uninstallData = processor.value();

        processor.remove(uninstallData);
    }
}
