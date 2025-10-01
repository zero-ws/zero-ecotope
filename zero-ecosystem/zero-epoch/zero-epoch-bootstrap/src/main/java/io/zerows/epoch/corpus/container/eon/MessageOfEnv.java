package io.zerows.epoch.corpus.container.eon;

/**
 * @author lang : 2024-04-22
 */ // ---------- C: io.vertx.up.running.ZeroMotor
public interface MessageOfEnv {

    // ---------- C: io.vertx.up.stored.anima.Verticles
    interface Verticle {
        String END = "( {3} ) The bottle {0} has been deployed " +
            "{1} instances successfully. id = {2}.";
        String FAILED = "( {3} ) The bottle {0} has been deployed " +
            "{1} instances failed. id = {2}, cause = {3}.";
        String STOPPED = "( {2} ) The bottle {0} has been undeployed " +
            " successfully, id = {1}.";
    }

    // ---------- io.vertx.up.stored.anima.Scatter
    interface Scatter {
        String CODEX = "( {0} Rules ) Zero system scanned the folder /codex/ " +
            "to pickup {0} rule definition files.";

    }

    /**
     * @author lang : 2024-04-22
     */
    interface OnOff {
        String AGENT_DEFINED = "User defined agent {0} of type = {1}, " +
            "the default will be overwritten.";
        String RPC_ENABLED = "( Micro -> Rpc ) Zero system detected the rpc server is Enabled. ";
    }
}
