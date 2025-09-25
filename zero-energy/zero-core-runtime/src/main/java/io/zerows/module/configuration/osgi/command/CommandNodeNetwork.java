package io.zerows.module.configuration.osgi.command;

import io.zerows.ams.constant.VString;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.option.ClusterOptions;
import io.zerows.module.configuration.store.OCacheNode;
import io.zerows.module.metadata.zdk.running.OCommand;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-22
 */
public class CommandNodeNetwork implements OCommand {
    private final boolean isBundleOnly;

    public CommandNodeNetwork(final boolean isBundleOnly) {
        this.isBundleOnly = isBundleOnly;
    }

    @Override
    public void execute(final Bundle caller) {
        if (this.isBundleOnly) {
            // TODO: 横向扩展命令
            System.out.println("Extend in future....");
        } else {
            final OCacheNode cache = OCacheNode.of(caller);
            final NodeNetwork network = cache.network();

            System.out.println("Network Information:");
            this.outputNetwork(network);
        }
    }

    private void outputNetwork(final NodeNetwork network) {
        final StringBuilder networkInfo = new StringBuilder();
        final ClusterOptions cluster = network.cluster();
        final boolean isEnabled = Objects.nonNull(cluster) && cluster.isEnabled();
        networkInfo.append(VString.INDENT).append("Enable Cluster: ")
            .append(isEnabled).append(VString.NEW_LINE);
        networkInfo.append(VString.INDENT).append("Enable Rpc: ")
            .append(network.okRpc()).append(VString.NEW_LINE);
        networkInfo.append(VString.INDENT).append("Enabled WebSocket: ")
            .append(network.okRpc()).append(VString.NEW_LINE);

        final Set<String> vertx = network.vertxOptions().keySet();
        networkInfo.append(VString.INDENT).append("Vertx Instances: ")
            .append(vertx.size()).append(VString.NEW_LINE);

        System.out.println(networkInfo);
    }
}
