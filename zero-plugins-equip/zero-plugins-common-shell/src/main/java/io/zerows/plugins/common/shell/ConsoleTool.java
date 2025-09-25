package io.zerows.plugins.common.shell;

import io.zerows.ams.constant.VValue;
import io.vertx.core.Future;
import io.zerows.core.util.Ut;
import io.zerows.plugins.common.shell.atom.CommandAtom;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.exception.BootCommandParseException;
import io.zerows.plugins.common.shell.exception.BootCommandUnknownException;
import io.zerows.plugins.common.shell.exception.BootPluginMissingException;
import io.zerows.plugins.common.shell.refine.Sl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ConsoleTool {

    static Future<CommandLine> parseAsync(final String[] args, final List<CommandAtom> definition) {
        /*
         * LineParser
         */
        final CommandLineParser parser = new DefaultParser();
        /*
         * Command ToolVerifier
         * The command must existing in your configuration files
         */
        final String commandName = args[0];
        return findAsync(commandName, definition).compose(command -> {
            try {
                /*
                 * Command valid
                 */
                final CommandLine parsed = parser.parse(command.options(), args);
                return Future.succeededFuture(parsed);
            } catch (final ParseException ex) {
                return Ut.Bnd.failOut(BootCommandParseException.class, ConsoleTool.class, Ut.fromJoin(args), ex);
            }
        });
    }

    static Future<EmCommand.TermStatus> runAsync(final CommandLine parsed, final List<CommandAtom> commands,
                                                 final Function<Commander, Commander> binder) {
        /*
         * Found command inner run method, double check for CommandAtom
         */
        final List<String> args = parsed.getArgList();
        return findAsync(args.get(VValue.IDX), commands).compose(command -> {
            /*
             * Create CommandArgs
             */
            final CommandInput input = getInput(parsed)
                .bind(command)
                .bind(commands);
            /*
             * Commander
             */
            final Commander commander;
            if (EmCommand.Type.SYSTEM == command.getType()) {
                /*
                 * Sub-System call
                 */
                commander = Ut.instance(ConsoleCommander.class);
            } else {
                /*
                 * Infusion processing
                 * instance instead of single for shared usage
                 */
                commander = Ut.instance(command.getPlugin());
                if (Objects.isNull(commander)) {
                    /*
                     * Could not be found
                     */
                    return Ut.Bnd.failOut(BootPluginMissingException.class, ConsoleTool.class,
                        command.getName() + ", ( " + command.getPlugin() + " )");
                }
            }
            /*
             * binder processing
             */
            Sl.welcomeCommand(command);
            return binder.apply(commander.bind(command)).executeAsync(input);
        });
    }

    private static CommandInput getInput(final CommandLine parsed) {
        final List<String> names = new ArrayList<>();
        final List<String> values = new ArrayList<>();
        Arrays.stream(parsed.getOptions()).forEach(option -> {
            final String name = option.getOpt();
            final String value = parsed.getOptionValue(name);
            names.add(name);
            values.add(value);
        });
        return CommandInput.create(names, values);
    }

    private static Future<CommandAtom> findAsync(final String commandName, final List<CommandAtom> commands) {
        final CommandAtom atom = commands.stream()
            /*
             * Filter by commandName here
             */
            .filter(each -> commandName.equals(each.getSimple()) || commandName.equals(each.getName()))
            .findAny().orElse(null);


        if (Objects.isNull(atom)) {
            /*
             * Unknown command of input throw out exception
             */
            return Ut.Bnd.failOut(BootCommandUnknownException.class, ConsoleTool.class, commandName);
        }


        if (atom.valid()) {
            /*
             * Returned Command Atom
             */
            return Future.succeededFuture(atom);
        } else {
            return Ut.Bnd.failOut(BootPluginMissingException.class, ConsoleTool.class, atom.getName());
        }
    }
}
