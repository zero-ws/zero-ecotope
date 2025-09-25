package io.zerows.module.metadata.osgi.command;

import io.vertx.core.json.JsonObject;
import io.zerows.module.metadata.store.OCacheFailure;
import io.zerows.module.metadata.zdk.running.OCommand;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-22
 */
public class CommandFailureSize implements OCommand {

    @Override
    public void execute(final Bundle caller) {
        // 提取异常表
        final JsonObject stored = OCacheFailure.entireError();
        // 拉取异常报表
        System.out.println("There are " + stored.size() + " errors stored in environment.");
    }
}
