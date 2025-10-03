package io.zerows.epoch.osgi.metadata.command;

import io.zerows.platform.constant.VString;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.running.OCommand;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.util.Objects;

interface ServiceT {
    static void bundleOr(final Bundle caller) {
        final ServiceReference<?>[] references = caller.getRegisteredServices();
        if (Objects.isNull(references) || 0 == references.length) {
            System.out.println("No service registered in current bundle. name = " + caller.getSymbolicName());
            return;
        }
        final StringBuilder builder = new StringBuilder();
        builder
            .append("Bundle: ( id = ").append(caller.getBundleId())
            .append(", name = ").append(caller.getSymbolicName()).append(" ) \n");
        builder.append("Service Registered List: ");
        builder.append(VString.NEW_LINE);
        for (final ServiceReference<?> reference : references) {
            builder.append(VString.INDENT).append(reference).append(VString.NEW_LINE);
            final Bundle[] using = reference.getUsingBundles();
            if (Objects.nonNull(using)) {
                for (final Bundle bundle : using) {
                    builder.append(VString.INDENT_2).append(bundle.getSymbolicName()).append(VString.NEW_LINE);
                }
            }
        }
        System.out.print(builder);
    }
}

/**
 * @author lang : 2024-04-28
 */
public class CommandService implements OCommand {
    @Override
    public void execute(final Bundle caller) {
        Ut.Bnd.commandRun(caller, ServiceT::bundleOr);
    }
}
