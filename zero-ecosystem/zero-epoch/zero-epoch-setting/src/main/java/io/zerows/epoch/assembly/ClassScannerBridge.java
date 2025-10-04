package io.zerows.epoch.assembly;

import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-17
 */
@SuppressWarnings("all")
class ClassScannerBridge implements ClassScanner {

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final ClassScanner scanner;
        if (Objects.isNull(bundle)) {
            scanner = CCT_SCANNER.pick(ClassScannerCommon::new,
                ClassScannerCommon.class.getName());
        } else {
            scanner = CCT_SCANNER.pick(ClassScannerBundle::new,
                ClassScannerBundle.class.getName());
        }
        return scanner.scan(bundle);
    }
}
