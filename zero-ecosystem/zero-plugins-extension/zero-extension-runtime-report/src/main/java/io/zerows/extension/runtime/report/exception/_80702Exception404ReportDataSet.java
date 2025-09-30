package io.zerows.extension.runtime.report.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80702Exception404ReportDataSet extends VertxWebException {
    public _80702Exception404ReportDataSet(final String reportId) {
        super(ERR._80702, reportId);
    }
}
