package io.zerows.module.assembly.osgi;

import io.zerows.module.assembly.osgi.command.CommandClasses;
import io.zerows.module.assembly.osgi.command.CommandClassesBID;
import io.zerows.module.metadata.eon.OConstant;
import io.zerows.module.metadata.zdk.AbstractCommand;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;

import java.util.Map;

/**
 * @author lang : 2024-05-02
 */
public class RuntimeAssemblyCommand extends AbstractCommand {

    public static String[] COMMANDS = new String[]{
        "zclasses"
    };

    public RuntimeAssemblyCommand(final BundleContext context) {
        super(context);
    }

    @Descriptor("View scanned classes of all bundle.")
    public String zclasses(@Parameter(names = {"bundle", "-b"}, absentValue = "") final String bundle) {
        return this.execCommon(bundle, Map.of(
            // classes bundle {bundleId}
            OConstant.CMD_BY_BUNDLE_ID, new CommandClassesBID(),
            // classes bundle all
            "all", new CommandClasses()
        ));
    }
}
