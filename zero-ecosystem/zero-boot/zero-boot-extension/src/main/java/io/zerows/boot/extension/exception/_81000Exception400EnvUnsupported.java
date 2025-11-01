package io.zerows.boot.extension.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * <pre>
 * - prod：生产环境专用命令。
 * - dev：开发环境专用命令。
 * - home：本地环境专用命令。
 * - zw：招为云环境。
 * </pre>
 *
 * @author lang : 2025-09-30
 */
public class _81000Exception400EnvUnsupported extends VertxWebException {
    public _81000Exception400EnvUnsupported(final String env) {
        super(ERR._81000, env);
    }
}
