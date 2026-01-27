package io.zerows.epoch.jigsaw;

import io.r2mo.function.Fn;
import io.vertx.core.Vertx;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.exception._60012Exception500PreConfigure;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * 启动专用检查器，用于检查缺失的部分防止调用启动器
 *
 * @author lang : 2025-11-04
 */
public class NodePre {

    public static boolean ensureDB(final Vertx vertxRef) {
        HConfig infix = NodeStore.findInfix(vertxRef, EmApp.Native.DATABASE);
        Fn.jvmKo(Objects.isNull(infix), _60012Exception500PreConfigure.class, EmApp.Native.DATABASE.name());

        infix = NodeStore.findExtension(vertxRef, "excel");
        Fn.jvmKo(Objects.isNull(infix), _60012Exception500PreConfigure.class, "excel");

        return true;
    }
}
