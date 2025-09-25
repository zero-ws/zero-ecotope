package io.zerows.extension.runtime.ambient.configuration;

import io.zerows.core.web.model.extension.HExtension;
import io.zerows.extension.runtime.ambient.eon.AtConstant;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import org.junit.Test;

/**
 * @author lang : 2024-05-08
 */
public class EquipAtTestCase {

    @Test
    public void testExtensionAt() {
        final MDConfiguration configuration = HExtension.getOrCreate(AtConstant.BUNDLE_SYMBOLIC_NAME);

        System.out.println(configuration.id());
    }
}
