package io.zerows.unity;

import io.r2mo.typed.cc.Cc;

interface INFO {


    String RPC_RESULT = "( Rpc -> thenRpc ) Client = {4}, Ipc ( {0},{1} ) with params {2}, response data is {3}.";

    interface UxJob {

        String JOB_START = "( UxJob ) The job {0} has been started with timeId: {1}.";
        String JOB_STOP = "( UxJob ) The job {0} has been stopped and removed.";
        String JOB_RESUME = "( UxJob ) The job {0} will be resume.";
    }
}

interface CACHE {
    Cc<String, UxLdap> CC_LDAP = Cc.openThread();
}
