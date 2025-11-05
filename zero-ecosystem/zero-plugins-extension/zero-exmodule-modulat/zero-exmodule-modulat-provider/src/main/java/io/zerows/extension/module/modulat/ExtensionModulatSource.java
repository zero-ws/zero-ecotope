package io.zerows.extension.module.modulat;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-11-05
 */
public class ExtensionModulatSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionModulatSource.class, args);
        final ExtensionModulatGeneration configuration = new ExtensionModulatGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
