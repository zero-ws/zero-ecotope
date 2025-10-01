package io.zerows.epoch.corpus.configuration.osgi;

import io.zerows.epoch.constant.VString;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.configuration.osgi.command.CommandNodeNetwork;
import io.zerows.epoch.corpus.configuration.osgi.command.CommandNodeVertx;
import io.zerows.epoch.corpus.metadata.eon.OConstant;
import io.zerows.epoch.corpus.metadata.zdk.AbstractCommand;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;

import java.util.Map;

/**
 * @author lang : 2024-04-17
 */
class RuntimeConfigurationCommand extends AbstractCommand {

    public static String[] COMMANDS = new String[]{
        "znode"
    };

    public RuntimeConfigurationCommand(final BundleContext context) {
        super(context);
    }

    @Descriptor("View the node information of ( network / vertx ).")
    public String znode(@Parameter(names = {"network", "-n"}, absentValue = "") final String network,
                        @Parameter(names = {"vertx", "-v"}, absentValue = "") final String vertx) {
        if (Ut.isOneOk(network, vertx)) {
            return this.execCommon(network, Map.of(
                OConstant.CMD_BY_BUNDLE_ID, new CommandNodeNetwork(true),
                "all", new CommandNodeNetwork(false)
            ));
        }


        if (Ut.isOneOk(vertx, network)) {
            return this.execCommon(vertx, Map.of(
                OConstant.CMD_BY_BUNDLE_ID, new CommandNodeVertx(true),
                "all", new CommandNodeVertx(false)
            ));
        }


        System.out.println("The condition is not matched. network = " + network + ", vertx = " + vertx);
        return VString.EMPTY;
    }
}
