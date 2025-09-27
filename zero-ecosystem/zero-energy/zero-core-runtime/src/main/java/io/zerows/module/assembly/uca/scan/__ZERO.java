package io.zerows.module.assembly.uca.scan;

/**
 * @author lang : 2024-04-19
 */
interface INFO {

    String PLUGIN = "Plugins {} has been scanned with @Infusion. ";

    String QUEUE = "( {0} Queue ) The Zero system has found {0} components of @Queue.";

    String ENDPOINT = "( {0} EndPoint ) The Zero system has found {0} components of @EndPoint.";

    String WORKER = "( Worker ) The Zero system has found {0} components of @Worker.";

    String AGENT = "( Agent ) The Zero system has found {0} components of @Agent.";

    String RPC = "( Rpc ) The Zero system has found {0} components of @Agent.";
}
