package io.zerows.extension.runtime.report.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author lang : 2024-10-11
 */
public class _404ReportMissingException extends WebException {
    public _404ReportMissingException(final Class<?> target, final String reportId) {
        super(target, reportId);
    }

    @Override
    public int getCode() {
        return -80701;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
