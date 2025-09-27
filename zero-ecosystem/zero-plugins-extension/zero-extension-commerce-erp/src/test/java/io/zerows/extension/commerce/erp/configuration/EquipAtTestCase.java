package io.zerows.extension.commerce.erp.configuration;

import io.zerows.core.web.model.extension.HExtension;
import io.zerows.extension.commerce.erp.eon.ErpConstant;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;

/**
 * @author lang : 2024-05-08
 */
public class EquipAtTestCase {

    public void testExtensionAt() {
        final MDConfiguration configuration = HExtension.getOrCreate(ErpConstant.BUNDLE_SYMBOLIC_NAME);

        System.out.println(configuration.id());
    }
}
