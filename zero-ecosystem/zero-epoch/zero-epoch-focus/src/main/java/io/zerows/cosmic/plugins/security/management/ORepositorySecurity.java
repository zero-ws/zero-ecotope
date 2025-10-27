package io.zerows.cosmic.plugins.security.management;

import io.zerows.cosmic.plugins.security.InquirerWall;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.management.ORepository;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Set;

/**
 * @author lang : 2024-04-22
 */
public class ORepositorySecurity extends AbstractAmbiguity implements ORepository {

    public ORepositorySecurity(final HBundle bundle) {
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
        final Inquirer<Set<KSecurity>> scanner = Ut.singleton(InquirerWall.class);
        processor.add(scanner.scan(OCacheClass.entireValue()));
    }

    @Override
    public void whenRemove() {
        final OCacheSecurity processor = OCacheSecurity.of(this.caller());

        final Set<KSecurity> uninstallData = processor.value();

        processor.remove(uninstallData);
    }
}
