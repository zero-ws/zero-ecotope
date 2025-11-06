package io.zerows.extension.module.graphic;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-11-05
 */
public class ExtensionGraphicSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionGraphicSource.class, args);
        final ExtensionGraphicGeneration configuration = new ExtensionGraphicGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
