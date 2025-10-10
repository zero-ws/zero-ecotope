package io.zerows.cortex.metadata;

import io.zerows.platform.metadata.KApp;

import java.io.Serializable;

/**
 * @author lang : 2024-05-03
 */
public class RunApp extends KApp implements Serializable {

    private RunServerLegacy refServer;

    public RunApp(final String name) {
        super(name);
    }

    public RunServerLegacy refServer() {
        return this.refServer;
    }

    public RunApp refServer(final RunServerLegacy refServer) {
        this.refServer = refServer;
        return this;
    }
}
