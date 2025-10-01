package io.zerows.module.metadata.osgi;

import io.zerows.epoch.constant.VString;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.eon.OConstant;
import io.zerows.module.metadata.osgi.command.*;
import io.zerows.module.metadata.zdk.AbstractCommand;
import io.zerows.module.metadata.zdk.running.OCommand;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;

import java.util.Map;

/**
 * @author lang : 2024-04-17
 */
class RuntimeMetadataCommand extends AbstractCommand {

    public static String[] COMMANDS = new String[]{
        "zfailure",
        "zservice",
        "zcache",
        "zexit"
    };

    public RuntimeMetadataCommand(final BundleContext context) {
        super(context);
    }

    @Descriptor("View the information of stored exceptions.")
    public String zfailure(@Parameter(names = {"info", "-i"}, absentValue = "") final String info,
                           @Parameter(names = {"error", "-e"}, absentValue = "") final String error) {
        if (Ut.isOneOk(error, info)) {
            return this.execCommon(error, Map.of(
                // failure error {bundleId}
                OConstant.CMD_BY_BUNDLE_ID, new CommandFailureBundle(),
                // failure error all
                "all", new CommandFailureAll(true),
                // failure error size
                "size", new CommandFailureSize()
            ));
        }


        if (Ut.isOneOk(info, error)) {
            return this.execCommon(info, Map.of(
                // failure info all
                "all", new CommandFailureAll(false)
            ));
        }


        System.out.println("The condition is not matched. error = " + error + ", info = " + info);
        return VString.EMPTY;
    }

    @Descriptor("View the information of services of {bundleId}.")
    public String zservice(@Parameter(names = {"bundle", "-b"}, absentValue = "") final String bundle) {
        return this.execCommon(bundle, Map.of(
            // service bundle {bundleId}
            OConstant.CMD_BY_BUNDLE_ID, new CommandServiceBID(),
            // service bundle all
            "all", new CommandService()
        ));
    }

    @Descriptor("View the information of cache.")
    public String zcache() {
        final OCommand command = new CommandCache();
        command.execute(this.context.getBundle());
        return VString.EMPTY;
    }

    @Descriptor("Exit from current framework, shutdown OSGI.")
    public String zexit() {
        System.exit(0);
        return VString.EMPTY;
    }
}
