package io.zerows.extension.module.tpl;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

/**
 * @author lang : 2025-11-05
 */
public class ExtensionTPLSource {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(ExtensionTPLSource.class, args);
        final ExtensionTPLGeneration configuration = new ExtensionTPLGeneration();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
