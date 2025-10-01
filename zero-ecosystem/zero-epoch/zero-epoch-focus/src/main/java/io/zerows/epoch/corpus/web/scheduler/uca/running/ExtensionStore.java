package io.zerows.epoch.corpus.web.scheduler.uca.running;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Bridge for different JobStore
 */
class ExtensionStore implements JobStore {
    private static final JobConfig CONFIG = JobPin.getConfig();
    private transient JobStore reference;
    private transient boolean isExtension;

    ExtensionStore() {
        if (Objects.nonNull(CONFIG)) {
            final Class<?> storeCls = CONFIG.getStore().getComponent();
            Optional.ofNullable(storeCls).ifPresent(clazz -> {
                this.reference = Ut.instance(clazz);
                if (Objects.nonNull(this.reference)) {
                    this.isExtension = true;
                }
            });
        }
    }

    @Override
    public Set<Mission> fetch() {
        return this.extensionCall(HashSet::new, () -> this.reference.fetch());
    }

    @Override
    public Mission fetch(final String name) {
        return this.extensionCall(() -> null, () -> this.reference.fetch(name));
    }

    @Override
    public JobStore remove(final Mission mission) {
        if (this.isExtension) {
            this.reference.remove(mission);
        }
        return this;
    }

    @Override
    public JobStore update(final Mission mission) {
        if (this.isExtension) {
            this.reference.update(mission);
        }
        return this;
    }

    @Override
    public JobStore add(final Mission mission) {
        if (this.isExtension) {
            this.reference.add(mission);
        }
        return this;
    }

    private <T> T extensionCall(final Supplier<T> defaultSupplier, final Supplier<T> extension) {
        if (this.isExtension) {
            return extension.get();
        } else {
            return defaultSupplier.get();
        }
    }
}
