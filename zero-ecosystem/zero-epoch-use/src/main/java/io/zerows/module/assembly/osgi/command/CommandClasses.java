package io.zerows.module.assembly.osgi.command;

import io.zerows.epoch.constant.VString;
import io.zerows.core.util.Ut;
import io.zerows.epoch.enums.VertxComponent;
import io.zerows.module.metadata.store.OCacheClass;
import io.zerows.module.metadata.zdk.running.OCommand;
import org.osgi.framework.Bundle;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

interface ServiceT {
    static void bundleOr(final Bundle caller) {
        final StringBuilder builder = new StringBuilder();
        builder
            .append("Bundle: ( id = ").append(caller.getBundleId())
            .append(", name = ").append(caller.getSymbolicName()).append(" ) \n");
        builder.append("Scanned Classes: ");
        builder.append(VString.NEW_LINE);
        final OCacheClass processor = OCacheClass.of(caller);
        final Map<String, Class<?>> treeMap = new TreeMap<>();
        processor.value().forEach(classSet -> treeMap.put(classSet.getName(), classSet));
        treeMap.forEach((name, clazz) -> {
            builder.append(VString.INDENT).append(name);
            final VertxComponent type = processor.valueType(clazz);
            if (Objects.nonNull(type)) {
                builder.append(", Type = ").append(type);
            }
            builder.append(VString.NEW_LINE);
        });
        System.out.print(builder);
    }
}

/**
 * @author lang : 2024-05-02
 */
public class CommandClasses implements OCommand {

    @Override
    public void execute(final Bundle caller) {
        Ut.Bnd.commandRun(caller, ServiceT::bundleOr);
    }
}
