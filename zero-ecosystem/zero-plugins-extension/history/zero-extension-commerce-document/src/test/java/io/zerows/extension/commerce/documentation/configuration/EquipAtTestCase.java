package io.zerows.extension.commerce.documentation.configuration;

import io.zerows.core.web.model.uca.normalize.EquipAt;
import io.zerows.extension.commerce.documentation.eon.DmConstant;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import org.junit.Test;

/**
 * @author lang : 2024-05-08
 */
public class EquipAtTestCase {

    @Test
    public void testExtensionAt() {
        final MDConfiguration configuration = new MDConfiguration(DmConstant.BUNDLE_SYMBOLIC_NAME);
        final EquipAt component = EquipAt.of(configuration.id());
        component.initialize(configuration);

        System.out.println(configuration.id());
    }
}
