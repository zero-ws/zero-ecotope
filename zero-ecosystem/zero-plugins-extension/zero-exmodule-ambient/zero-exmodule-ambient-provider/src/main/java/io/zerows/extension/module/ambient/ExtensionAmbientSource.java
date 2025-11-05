package io.zerows.extension.module.ambient;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-11-05
 */
public class ExtensionAmbientSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionAmbientSource.class, args);
        final ExtensionAmbientGeneration configuration = new ExtensionAmbientGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
