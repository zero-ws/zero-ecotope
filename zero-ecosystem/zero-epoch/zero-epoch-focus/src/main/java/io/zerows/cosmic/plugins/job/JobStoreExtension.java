package io.zerows.cosmic.plugins.job;

import io.zerows.cosmic.plugins.job.metadata.Mission;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Bridge for different JobStore
 */
class JobStoreExtension implements JobStore {
    // 线程本地重入保护，防止递归调用导致 StackOverflow
    private final transient ThreadLocal<Boolean> inCall = ThreadLocal.withInitial(() -> Boolean.FALSE);
    // 延迟加载 reference，避免构造时立即访问外部资源
    private transient JobStore reference;
    private transient boolean isExtension;

    JobStoreExtension() {
        // 延迟初始化，不在构造器中调用 JobActor.ofStore()
    }

    @Override
    public Set<Mission> fetch() {
        this.ensureReference();
        if (Objects.isNull(this.reference)) {
            return new HashSet<>();
        }
        return this.extensionCall(HashSet::new, this.reference::fetch);
    }

    @Override
    public Mission fetch(final String name) {
        this.ensureReference();
        if (Objects.isNull(this.reference)) {
            return null;
        }
        return this.extensionCall(() -> null, () -> this.reference.fetch(name));
    }

    @Override
    public JobStore remove(final Mission mission) {
        this.ensureReference();
        if (Objects.isNull(this.reference)) {
            return this;
        }
        if (this.isExtension) {
            this.reference.remove(mission);
        }
        return this;
    }

    @Override
    public JobStore update(final Mission mission) {
        this.ensureReference();
        if (Objects.isNull(this.reference)) {
            return this;
        }
        if (this.isExtension) {
            this.reference.update(mission);
        }
        return this;
    }

    @Override
    public JobStore add(final Mission mission) {
        this.ensureReference();
        if (Objects.isNull(this.reference)) {
            return this;
        }
        if (this.isExtension) {
            this.reference.add(mission);
        }
        return this;
    }

    private <T> T extensionCall(final Supplier<T> defaultSupplier, final Supplier<T> extension) {
        // 保护性检查，确保 reference 已经初始化并且可用
        if (Objects.isNull(this.reference) || !this.isExtension || Boolean.TRUE.equals(this.inCall.get())) {
            // 如果当前正在进行 extensionCall，则返回默认值，避免递归
            return defaultSupplier.get();
        }
        try {
            this.inCall.set(Boolean.TRUE);
            return extension.get();
        } finally {
            this.inCall.set(Boolean.FALSE);
        }
    }

    private void ensureReference() {
        if (Objects.isNull(this.reference)) {
            final JobStore ref = JobActor.ofStore();
            // 防止循环引用：如果返回的实现是本类或 JobStoreUnity，则视为无扩展实现
            if (Objects.isNull(ref) || ref == this || ref instanceof JobStoreUnity) {
                this.reference = null;
                this.isExtension = false;
            } else {
                this.reference = ref;
                this.isExtension = true;
            }
        }
    }
}
