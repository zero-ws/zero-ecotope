package io.zerows.cortex.metadata;

import io.zerows.platform.apps.KApp;

import java.io.Serializable;

/**
 * @author lang : 2024-05-03
 */
public class RunApp extends KApp implements Serializable {

    private RunServer refServer;

    public RunApp(final String name) {
        super(name);
    }

    public RunServer refServer() {
        return this.refServer;
    }

    public RunApp refServer(final RunServer refServer) {
        this.refServer = refServer;
        return this;
    }
}
