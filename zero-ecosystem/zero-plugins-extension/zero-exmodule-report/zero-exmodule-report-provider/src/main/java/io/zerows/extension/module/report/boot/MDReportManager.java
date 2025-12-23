package io.zerows.extension.module.report.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDReportManager extends MDModuleManager<Boolean, Boolean> {
    private static MDReportManager INSTANCE;

    private MDReportManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDReportManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDReportManager();
        }
        return INSTANCE;
    }
}
