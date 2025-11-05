package io.zerows.extension.module.report.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80703Exception400ReportDimType extends VertxWebException {
    public _80703Exception400ReportDimType(final String reportId, final String dimKey) {
        super(ERR._80703, reportId, dimKey);
    }
}
