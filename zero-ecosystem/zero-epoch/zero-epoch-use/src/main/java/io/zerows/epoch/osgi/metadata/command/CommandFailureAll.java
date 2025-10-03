package io.zerows.epoch.osgi.metadata.command;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.mem.OCacheFailure;
import io.zerows.epoch.sdk.metadata.running.OCommand;
import org.osgi.framework.Bundle;

import java.util.Set;

/**
 * @author lang : 2024-04-22
 */
public class CommandFailureAll implements OCommand {

    private boolean isError = false;

    public CommandFailureAll(final boolean isError) {
        this.isError = isError;
    }

    @Override
    public void execute(final Bundle caller) {

        final JsonObject stored = this.isError ? OCacheFailure.entireError() : OCacheFailure.entireFailure();
        final String output = this.buildOutput(stored);
        // 拉取异常报表
        System.out.println("Stored error data: \n" + output);
    }

    private String buildOutput(final JsonObject stored) {
        final StringBuilder builder = new StringBuilder();
        final Set<String> fields = stored.fieldNames();
        if (fields.isEmpty()) {
            builder.append("No data stored in environment.");
        } else {
            fields.forEach(field -> builder.append(VString.INDENT).append(field)
                .append(" = ").append(stored.getValue(field)).append(VString.NEW_LINE));
        }
        return builder.toString();
    }
}
