package io.zerows.platform.constant;

/**
 * @author lang : 2023/4/24
 */
public interface VMessage {

    interface KEnvironment {
        String DEVELOPMENT = "OS = {0},  `{1}` file has been found! DEVELOPMENT connected.";

        String ENV = "Final Environment Variables: {0}\n";
    }

    // ---------- io.horizon.storage.specification.HFS
    interface HFS {

        String IO_CMD_RM = "I/O Command: `rm -rf {0}`";
        String IO_CMD_MKDIR = "I/O Command: `mkdir -P {0}`";
        String IO_CMD_MOVE = "I/O Command: `mv {0} {1}`";

        String IO_CMD_CP = "I/O Command: `cp -rf {0} {1}`, Method = {2}";

        String ERR_CMD_CP = "One of folder: ({0},{1}) does not exist, could not action cp command.";
    }

    interface BootIo {
        String LAUNCHER_COMPONENT = "You have selected launcher: {0}";
    }

    interface HOn {
        String COMPONENT = "The component = {0}, config = {1}";
        String COMPONENT_CONFIG = "The json data = {0}";
        String COMPONENT_ARGS = "The args = {0}, size = {1}";
    }
}
