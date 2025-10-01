package io.zerows.epoch.bootplus.extension.uca.console;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.migration.MigrateStep;
import io.zerows.epoch.bootplus.extension.migration.restore.MetaLimit;
import io.zerows.epoch.bootplus.extension.refine.Ox;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.runtime.ambient.agent.service.application.InitStub;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class InitInstruction extends AbstractInstruction {
    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final String appName = this.inString(args, "a");
        /*
         * appName为null，直接获取app
         */
        if (appName == null) {
            return this.partyA().compose(okA -> this.defaultValue(okA.configArk()));
        } else {
            return this.partyB(appName).compose(okB -> this.defaultValue(okB.configArk()));
        }
    }

    private Future<EmCommand.TermStatus> defaultValue(final HArk ark) {
        final InitStub stub = Ox.pluginInitializer();
        /*
         * 全部导入完成，执行初始化
         */
        final HApp app = ark.app();
        return stub.initModeling(app.name()).compose(inited -> {
            /*
             * 执行初始化
             */
            Sl.output("业务环境初始化完成！");
            final MigrateStep step = new MetaLimit(this.environment);
            /* 建模修正数据 */
            return step.bind(ark).procAsync(new JsonObject()).compose(config -> {
                Sl.output("模型数据修正完成！");
                return Ux.future(EmCommand.TermStatus.SUCCESS);
            });
        }).otherwise(Sl::failError);
    }
}
