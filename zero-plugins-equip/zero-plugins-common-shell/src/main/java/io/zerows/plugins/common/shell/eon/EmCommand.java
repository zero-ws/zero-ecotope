package io.zerows.plugins.common.shell.eon;

/**
 * @author lang : 2024-04-22
 */
public class EmCommand {
    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum Type {
        SYSTEM,     // It means current command could get to sub-system of zero
        COMMAND,    // Current command is executor for plugin here
        DEFAULT;    // Default command that zero framework provide
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum TermStatus {
        SUCCESS,    // Success capture input line
        FAILURE,    // Failure happened
        WAIT,       // Wait for input status processing
        EXIT,       // Will quit current mode
    }
}
