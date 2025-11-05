package io.zerows.extension.module.report.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80701Exception404ReportMissing extends VertxWebException {
    public _80701Exception404ReportMissing(final String reportId) {
        super(ERR._80701, reportId);
    }
}
