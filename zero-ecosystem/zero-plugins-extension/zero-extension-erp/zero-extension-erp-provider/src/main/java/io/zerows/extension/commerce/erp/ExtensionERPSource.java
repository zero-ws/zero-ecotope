package io.zerows.extension.commerce.erp;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-10-20
 */
public class ExtensionERPSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionERPSource.class, args);
        final ExtensionERPGeneration configuration = new ExtensionERPGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
