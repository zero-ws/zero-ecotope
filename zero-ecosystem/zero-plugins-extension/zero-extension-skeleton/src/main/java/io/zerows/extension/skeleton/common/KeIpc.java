package io.zerows.extension.skeleton.common;

/**
 * Standard Ipc for Zero extension module
 * It's for communication
 */
public interface KeIpc {
    interface Workflow {
        /*
         * Event Addr Prefix for workflow
         * This findRunning will be shared between zero-ambient / zero-wf
         * */
        String EVENT = "Ἀτλαντὶς νῆσος://Ροή εργασίας/";
    }

    /*
     * Rbac Ipc
     */
    interface Sc {
        /* Ipc for verify token */
        String IPC_TOKEN_VERIFY = "IPC://TOKEN/VERIFY";
        /* Ipc for access token */
        String IPC_TOKEN_ACCESS = "IPC://TOKEN/ACCESS";
    }
}
