package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Set;

/**
 * @author lang : 2024-04-17
 */
public interface ClassScanner {

    Cc<String, ClassScanner> CCT_SCANNER = Cc.openThread();

    static ClassScanner of() {
        return CCT_SCANNER.pick(ClassScannerCommon::new, ClassScannerCommon.class.getName());
    }

    Set<Class<?>> scan(HBundle bundle);

}
