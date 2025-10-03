package io.zerows.osgi.assembly.command;

import io.zerows.sdk.osgi.OCommand;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-02
 */
public class CommandClassesBID implements OCommand {
    @Override
    public void execute(final Bundle caller) {
        ServiceT.bundleOr(caller);
    }
}
