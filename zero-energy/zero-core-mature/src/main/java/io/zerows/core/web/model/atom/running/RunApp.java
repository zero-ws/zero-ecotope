package io.zerows.core.web.model.atom.running;

import io.zerows.common.app.KApp;

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
