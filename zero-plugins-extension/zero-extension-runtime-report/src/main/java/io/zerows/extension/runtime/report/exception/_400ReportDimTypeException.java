package io.zerows.extension.runtime.report.exception;

import io.zerows.core.exception.WebException;

/**
 * @author lang : 2024-10-29
 */
public class _400ReportDimTypeException extends WebException {
    public _400ReportDimTypeException(final Class<?> target, final String reportId) {
        super(target, reportId);
    }

    @Override
    public int getCode() {
        return -80703;
    }
}
