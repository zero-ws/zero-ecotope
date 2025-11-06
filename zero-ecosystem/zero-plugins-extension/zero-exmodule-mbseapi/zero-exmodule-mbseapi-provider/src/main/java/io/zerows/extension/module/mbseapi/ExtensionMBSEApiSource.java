package io.zerows.extension.module.mbseapi;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-11-05
 */
public class ExtensionMBSEApiSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionMBSEApiSource.class, args);
        final ExtensionMBSEApiGeneration configuration = new ExtensionMBSEApiGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
