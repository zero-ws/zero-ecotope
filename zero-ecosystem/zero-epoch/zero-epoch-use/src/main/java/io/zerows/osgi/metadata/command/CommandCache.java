package io.zerows.osgi.metadata.command;

import io.zerows.platform.constant.VString;
import io.zerows.sdk.management.OCache;
import io.zerows.sdk.osgi.OCommand;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-30
 */
public class CommandCache implements OCommand {
    @Override
    public void execute(final Bundle caller) {
        System.out.println("Cache Information: ");
        final StringBuilder builder = new StringBuilder();
        OCache.REGISTRY.forEach((key, instanceSet) -> {
            builder
                .append(VString.INDENT).append("Bundle:").append(key).append(VString.NEW_LINE)
                .append(VString.INDENT).append("Size: ").append(instanceSet.size()).append(VString.NEW_LINE)
                .append(VString.INDENT).append("Component: ").append(VString.NEW_LINE);
            instanceSet.forEach(instance -> builder.append(VString.INDENT_2)
                .append(instance.getName()).append(VString.NEW_LINE));
        });
        System.out.print(builder);
    }
}
