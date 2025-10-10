package io.zerows.epoch.management;

import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertxLegacy;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-04-20
 */
@Deprecated
class OCacheNodeAmbiguity extends AbstractAmbiguity implements OCacheNode {

    private static final NodeNetwork NETWORK = new NodeNetwork();

    OCacheNodeAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    // -------------- 比较特殊，全部为全局方法，唯一的网络节点 -------------------
    @Override
    public NodeVertxLegacy valueGet(final String name) {
        return null; // NETWORK.get(name);
    }

    @Override
    public NodeNetwork network() {
        return NETWORK;
    }

    @Override
    public OCacheNode add(final NodeVertxLegacy nodeVertxLegacy) {
        // NETWORK.add(nodeVertxLegacy.name(), nodeVertxLegacy);
        return this;
    }

    @Override
    public OCacheNode remove(final NodeVertxLegacy nodeVertxLegacy) {
        // NETWORK.remove(nodeVertxLegacy.name());
        return this;
    }
}
