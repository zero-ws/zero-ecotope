package io.zerows.plugins.common.shell;

import io.zerows.ams.constant.em.Environment;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.plugins.common.shell.atom.CommandAtom;
import io.zerows.plugins.common.shell.atom.Terminal;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ConsoleInteract {

    private transient final Environment environment;
    private transient final Vertx vertx;

    private ConsoleInteract(final Vertx vertx, final Environment environment) {
        this.environment = environment;
        this.vertx = vertx;
    }

    public static ConsoleInteract start(final Vertx vertx, final Environment environment) {
        return new ConsoleInteract(vertx, environment);
    }

    void run(final String... args) {
        /* Welcome first */
        Sl.welcome();

        /* Environment and Input */
        Sl.welcomeCommand(this.environment);

        /* Create once */
        final Terminal terminal = Terminal.create(this.vertx);

        /* Must be wrapper here */
        this.run(terminal);
    }

    void run(final Terminal terminal) {
        final Consumer<Terminal> consumer = terminalRef -> {
            /* Environment input again */
            Sl.welcomeCommand(this.environment);
            /* Continue here */
            this.run(terminalRef);
        };
        terminal.run(handler -> {
            if (handler.succeeded()) {
                /* Process result of input */
                final String[] args = handler.result();

                /* Major code logical should returned Future<TermStatus> instead */
                final Future<EmCommand.TermStatus> future = this.runAsync(args);

                future.onComplete(callback -> {
                    if (callback.succeeded()) {
                        final EmCommand.TermStatus status = callback.result();
                        if (EmCommand.TermStatus.EXIT == status) {
                            /*
                             * EXIT -> EmApp End
                             */
                            System.exit(0);
                        } else {
                            /*
                             * SUCCESS, FAILURE
                             */
                            if (EmCommand.TermStatus.WAIT != status) {
                                consumer.accept(terminal);
                            }
                        }
                    } else {
                        consumer.accept(terminal);
                    }
                });
            } else {
                /* Error Input */
                Sl.failEmpty();
                consumer.accept(terminal);
            }
        });
    }

    private Future<EmCommand.TermStatus> runAsync(final String[] args) {
        /* Critical CommandOption */
        final List<CommandAtom> commands = Sl.commands();

        /* Parse Arguments */
        return ConsoleTool.parseAsync(args, commands)

            /* Execute Command */
            .compose(commandLine -> ConsoleTool.runAsync(commandLine, commands,

                /* Binder Function */
                commander -> commander.bind(this.environment).bind(this.vertx)))
            .otherwise(Sl::failError);
    }
}
