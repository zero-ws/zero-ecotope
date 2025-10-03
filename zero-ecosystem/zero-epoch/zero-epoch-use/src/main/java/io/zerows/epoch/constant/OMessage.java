package io.zerows.epoch.constant;

/**
 * @author lang : 2024-04-22
 */
public interface OMessage {
    /**
     * @author lang : 2024-04-17
     */
    interface Osgi {
        interface SERVICE {
            String REGISTER = "The service \"{0}\" with \"{1}\" has been registered successfully!";
            String DELAY = "Delay::Service \"{0}\" has been published!";
            String UNREGISTER = "The service \"{0}\" has been unregistered successfully!";
        }

        interface COMMAND {
            String REGISTER = "The command \"{0}\" is Ok for action!";
            String UNREGISTER = "The command \"{0}\" will be Invalid!";
        }

        interface BUNDLE {
            String START = "The bundle ({0}) has been started successfully!";
            String STOP = "The bundle ({0}) has been stopped successfully!";
        }
    }

    interface Measure {
        String REMOVE = "[ Meansure ] The {0} has been removed. ( instances = {1} )";
        String ADD = "[ Meansure ] The {0} has been added. ( instances = {1} ), worker = {2}";

    }

    // ----------- C: io.zerows.support.fn.FnZero
    interface Fn {
        String PROGRAM_NULL = "[ Program ] Null Input";
        String PROGRAM_QR = "[ Program ] Null Record in database";
    }
}
