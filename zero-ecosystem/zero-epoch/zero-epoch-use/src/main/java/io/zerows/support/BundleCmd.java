package io.zerows.support;

import io.zerows.epoch.constant.OMessage;
import io.zerows.epoch.sdk.osgi.OCommand;
import io.zerows.support.base.UtBase;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author lang : 2024-04-22
 */
class BundleCmd {

    static void commandBind(final BundleContext context,
                            final Class<?> commandCls,
                            final String... args) {
        final Hashtable<String, Object> props = configureCommand(args);
        context.registerService(commandCls.getName(), Ut.instance(commandCls, context), props);

        _Log.Log.command(commandCls).info(OMessage.Osgi.COMMAND.REGISTER,
            UtBase.fromJoin(args));
    }

    static OCommand commandBuild(final String value, final Map<String, OCommand> store) {
        if (!store.containsKey(value)) {
            System.out.println("(E) System could not find the component of :" + value);
            System.out.println("(E) Valid commands is: " + Ut.fromJoin(store.keySet()));
        }
        System.out.println("[ ZERO ] Command : " + value);
        return store.get(value);
    }

    static void commandRun(final Bundle caller, final Consumer<Bundle> executor) {
        final BundleContext context = caller.getBundleContext();
        final Bundle[] bundles = context.getBundles();
        for (final Bundle bundle : bundles) {
            final String name = bundle.getSymbolicName();
            if (name.startsWith("io.zerows")) {
                executor.accept(bundle);
                System.out.println("----------------------------------------");
            }
        }
    }

    private static Hashtable<String, Object> configureCommand(final String[] functions) {
        final Hashtable<String, Object> props = new Hashtable<>();
        props.put("osgi.command.scope", "zoi");
        props.put("osgi.command.function", functions);
        return props;
    }
}
