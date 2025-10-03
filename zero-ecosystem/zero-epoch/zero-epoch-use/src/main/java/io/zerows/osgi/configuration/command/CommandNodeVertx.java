package io.zerows.osgi.configuration.command;

import io.zerows.epoch.sdk.osgi.OCommand;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-22
 */
public class CommandNodeVertx implements OCommand {
    private final boolean isBundleOnly;

    public CommandNodeVertx(final boolean isBundleOnly) {
        this.isBundleOnly = isBundleOnly;
    }

    @Override
    public void execute(final Bundle caller) {
    }
}
