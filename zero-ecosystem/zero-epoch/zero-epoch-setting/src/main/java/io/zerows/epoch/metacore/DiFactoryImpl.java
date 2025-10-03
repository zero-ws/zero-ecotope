package io.zerows.epoch.metacore;

import com.google.inject.Injector;
import io.r2mo.typed.cc.Cc;
import io.zerows.management.OCacheClass;
import io.zerows.sdk.environment.Inquirer;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-01
 */
class DiFactoryImpl implements DiFactory {
    private static final Cc<String, Inquirer<Injector>> CC_GUICE = Cc.open();
    private static DiFactory INSTANCE;

    private DiFactoryImpl() {
    }

    static DiFactory of() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DiFactoryImpl();
        }
        return INSTANCE;
    }

    @Override
    public Injector build() {
        final String cacheKey = this.ambiguityKey();
        final Inquirer<Injector> inquirer = CC_GUICE
            .pick(InquirerGuice::new, cacheKey);
        // 不论哪个环境都会直接访问到 OCacheClass 中的核心数据结构
        return DiFactoryImpl.CC_SKELETON
            .pick(() -> this.build(inquirer, "Booting"), cacheKey);
    }

    @Override
    public Injector refresh() {
        final String cacheKey = this.ambiguityKey();
        final Inquirer<Injector> inquirer = CC_GUICE
            .pick(InquirerGuice::new, cacheKey);

        final Injector replaced = this.build(inquirer, "Refreshing");

        DiFactoryImpl.CC_SKELETON.get().put(cacheKey, replaced);
        return replaced;
    }

    private synchronized Injector build(final Inquirer<Injector> inquirer, final String flag) {
        final Set<Class<?>> storedClass = OCacheClass.entireValue();

        final long start = System.currentTimeMillis();
        final Injector injector = inquirer.scan(storedClass);
        final long end = System.currentTimeMillis();
        final long duration = end - start;
        Ut.Log.boot(this.getClass()).info(" {}ms / Zero DI Environment {}.... Size= {}",
            String.valueOf(duration), flag, String.valueOf(storedClass.size()));
        return injector;
    }

    private String ambiguityKey() {
        final Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        if (Objects.isNull(bundle)) {
            // 单机环境
            return DiFactoryImpl.class.getName();
        } else {
            // OSGI 环境
            return Ut.Bnd.keyCache(bundle);
        }
    }
}
