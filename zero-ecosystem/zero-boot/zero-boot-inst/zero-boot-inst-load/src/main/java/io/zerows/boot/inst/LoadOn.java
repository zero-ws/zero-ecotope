package io.zerows.boot.inst;

import io.zerows.platform.constant.VClassPath;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.metadata.KPathAtom;

/**
 * @author lang : 2025-11-03
 */
public class LoadOn {
    public static void runActivity(final Class<?> mainClass) {
        final String path = VClassPath.init.OOB;
        LoadInst.run(mainClass,
            path,                 // path     = init/oob/activity-rule
            VValue.FALSE,                // oob      = false
            "activity-rule"              // prefix   = "activity-rule"
        );
    }

    public static void runCab(final Class<?> mainClass) {
        final String path = VClassPath.init.oob.CAB;
        LoadInst.run(mainClass,
            path,                 // path     = init/oob/cab
            VValue.FALSE,                // oob      = false
            null                         // prefix   = null
        );
    }

    public static void runData(final Class<?> mainClass) {
        final String path = VClassPath.init.oob.DATA;
        LoadInst.run(mainClass,
            path,                 // path     = init/oob/data
            VValue.FALSE,                // oob      = false
            null                         // prefix   = null
        );
    }

    public static void runEnvironment(final Class<?> mainClass) {
        final String path = VClassPath.init.oob.ENVIRONMENT;
        LoadInst.run(mainClass,
            path,                 // path     = init/oob/environment
            VValue.FALSE,                // oob      = false
            null                         // prefix   = null
        );
    }

    public static void runPermission(final Class<?> mainClass, final String role) {
        final String path = VClassPath.init.oob.role.of(role);
        LoadInst.run(mainClass,
            path,                 // path     = init/oob/role/LANG.YU
            VValue.FALSE,                // oob      = false
            null                         // prefix   = null
        );
    }

    public static void runOob(final Class<?> mainClass, final String prefix) {
        final String path = VClassPath.init.OOB;
        LoadInst.run(mainClass,
            path,
            VValue.TRUE,
            prefix
        );
    }

    public static void runOob(final Class<?> mainClass) {
        final String path = VClassPath.init.OOB;
        LoadInst.run(mainClass,
            path,
            VValue.TRUE,
            null
        );
    }

    public static void atomLoad(final Class<?> mainClass, final KPathAtom pathAtom) {
        LoadInst.run(mainClass,
            pathAtom.path(),       // path      = init/oob/cmdb
            VValue.FALSE,                 // oob       = false
            null                          // prefix    = null
        );
    }

    public static void atomUi(final Class<?> mainClass, final KPathAtom pathAtom, final String prefix) {
        LoadInst.run(mainClass,
            pathAtom.atomUi(),
            VValue.FALSE,
            prefix
        );
    }
}
