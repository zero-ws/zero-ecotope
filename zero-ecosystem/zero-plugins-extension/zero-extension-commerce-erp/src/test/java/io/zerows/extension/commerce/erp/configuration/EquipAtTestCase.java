package io.zerows.extension.commerce.erp.configuration;

import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.extension.commerce.erp.eon.ErpConstant;

/**
 * @author lang : 2024-05-08
 */
public class EquipAtTestCase {

    public void testExtensionAt() {
        final MDConfiguration configuration = HExtension.getOrCreate(ErpConstant.BUNDLE_SYMBOLIC_NAME);

        System.out.println(configuration.id());
    }
}
