package io.zerows.plugins.common.shell.commander;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.util.Ut;
import io.zerows.plugins.common.shell.AbstractCommander;
import io.zerows.plugins.common.shell.atom.CommandAtom;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import org.apache.commons.cli.HelpFormatter;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * # 「Co」Command of Help
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HelpCommander extends AbstractCommander {

    private static final String ARG_COMMAND = "c";

    @Override
    public EmCommand.TermStatus execute(final CommandInput args) {
        final List<CommandAtom> atomList = this.getAtomList(args.atom());
        final ConcurrentMap<String, String> inputMap = args.get();
        if (inputMap.containsKey(ARG_COMMAND)) {
            final String commandValue = inputMap.get(ARG_COMMAND);
            final CommandAtom found = this.findAtom(atomList, commandValue);
            if (Objects.isNull(found)) {
                /*
                 * Command invalid
                 */
                Sl.failInvalid(commandValue);
                return EmCommand.TermStatus.FAILURE;
            } else {

                /*
                 * Valid Help
                 */
                this.printCommand(found);
                return EmCommand.TermStatus.SUCCESS;
            }
        } else {
            /*
             * No `command` provide
             */
            this.printCommands(atomList);
            return EmCommand.TermStatus.SUCCESS;
        }
    }

    private List<CommandAtom> getAtomList(final ConcurrentMap<String, CommandAtom> atomMap) {
        final Set<String> treeSet = new TreeSet<>(atomMap.keySet());
        final List<CommandAtom> atoms = new ArrayList<>();
        treeSet.stream().map(atomMap::get).forEach(atoms::add);
        return atoms;
    }

    private CommandAtom findAtom(final List<CommandAtom> atomList, final String command) {
        return atomList.stream()
            .filter(atom -> command.equals(atom.getName()) || command.equals(atom.getSimple()))
            .findAny().orElse(null);
    }

    private void printCommands(final List<CommandAtom> atoms) {
        final JsonObject config = Ut.valueJObject(this.atom.getConfig().getJsonObject("header"));
        final String name = config.containsKey("name") ? config.getString("name") : "Command Name";
        final String simple = config.containsKey("simple") ? config.getString("simple") : "Command Simple";
        final String description = config.containsKey("description") ? config.getString("description") : "Description";

        /* Format Table */
        final StringBuilder content = new StringBuilder();
        content.append(Sl.message(YmlCore.shell.welcome.message.HELP,
            () -> "Command List: ")).append("\n");
        content.append("------------------------------------------------------\n");
        content.append(Ut.rgbYellowB("%-32s", name));
        content.append(Ut.rgbYellowB("%-26s", simple));
        content.append(Ut.rgbYellowB("%-16s", description)).append("\n");
        content.append("------------------------------------------------------\n");

        /* Defined Map */
        this.printContent(content, atoms, " ");
        content.append("------------------------------------------------------");
        System.out.println(content);
    }

    private void printContent(final StringBuilder content, final List<CommandAtom> atoms, final String prefix) {
        atoms.forEach(atom -> {
            content.append(String.format("%-24s", prefix + " " + atom.getName()));
            content.append(String.format("%-20s", prefix + " " + atom.getSimple()));
            content.append(String.format("%-20s", atom.getDescription())).append("\n");
            /* Where it's SYSTEM */
            if (EmCommand.Type.SYSTEM == atom.getType()) {
                final List<CommandAtom> children = atom.getCommands();
                this.printContent(content, children, prefix + " - ");
            }
        });
    }

    private void printCommand(final CommandAtom atom) {
        /* header */
        final String header = Sl.message(YmlCore.shell.welcome.message.HEADER,
            () -> "Zero Framework Console/Shell!");
        /* command */
        String usage = Sl.message(YmlCore.shell.welcome.message.USAGE,
            () -> "Basic Syntax: <command> [options...]" +
                "\tCommand Name: {0}, Command Type: {1}" +
                "\tOptions Format: [-opt1 value1 -opt2 value2]");
        usage = MessageFormat.format(usage,
            Ut.rgbCyanB(atom.getName()),
            Ut.rgbCyanB(atom.getType().name()));

        /* Help */
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(240);
        formatter.printHelp(usage, header, atom.options(), null);
    }
}
