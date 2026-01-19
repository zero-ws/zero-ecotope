package io.zerows.plugins.security.oauth2;

import io.zerows.extension.skeleton.boot.ExtensionLauncher;
import io.zerows.platform.ENV;

public class SourceOAuth2Generator {

    public static void main(final String[] args) {
        final ExtensionLauncher launcher = ExtensionLauncher.create(SourceOAuth2Generator.class, args);
        final SourceOAuth2Generation configuration = new SourceOAuth2Generation();
        configuration.resolver(ENV::parseVariable);
        launcher.startGenerate(configuration);
    }
}
