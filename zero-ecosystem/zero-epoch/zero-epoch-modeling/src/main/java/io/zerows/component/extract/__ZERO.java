package io.zerows.component.extract;

interface INFO {

    String AGENT_HIT = "( Agent ) The standard bottle " +
        "{0} will be deployed.";

    String WORKER_HIT = "( Worker ) The worker vertical " +
        "{0} will be deployed.";

    String METHOD_IGNORE = "Method name = {0} has not annotated with " +
        "jakarta.ws.rs.[@GET,@POST,@PUT,@DELETE,@OPTIONS,@PATCH,@HEAD], ignored resolving.";

    String METHOD_MODIFIER = "( Ignored ) Method name = {0} access scope is invalid, " +
        "the scope must be public non-static.";
}
