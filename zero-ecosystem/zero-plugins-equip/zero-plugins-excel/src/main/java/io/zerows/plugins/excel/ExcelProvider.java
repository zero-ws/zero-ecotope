package io.zerows.plugins.excel;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * @author lang : 2025-10-31
 */
class ExcelProvider extends AddOnProvider<ExcelClient> {
    ExcelProvider(final AddOn<ExcelClient> addOn) {
        super(addOn);
    }
}
