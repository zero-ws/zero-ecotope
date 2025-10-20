package io.zerows.epoch.bootplus.extension.uca.console;

import io.r2mo.jce.common.HED;
import io.vertx.core.Future;
import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HEDInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput input) {
        final String waitFor = this.inString(input, "i");
        final String path = this.inString(input, "p");
        final String console;
        if (Ut.isNil(path)) {
            throw new RuntimeException("[ HED ] 公钥文件路径不能为空！");
        } else {
            final String publicKey = Ut.ioString(path);
            console = HED.encryptRSAPublic(waitFor, publicKey);
        }
        System.out.println("加密前：" + waitFor);
        System.out.println("加密后：" + console);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(console), null);
        return Ux.future(EmCommand.TermStatus.SUCCESS);
    }
}
