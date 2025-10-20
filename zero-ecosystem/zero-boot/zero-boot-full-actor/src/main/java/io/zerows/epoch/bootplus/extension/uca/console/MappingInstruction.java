package io.zerows.epoch.bootplus.extension.uca.console;

import io.zerows.epoch.bootplus.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;

import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MappingInstruction extends AbstractInstruction {

    @Override
    public EmCommand.TermStatus execute(final CommandInput args) {
        final String source = this.inFolder(args, "s", this::path);
        Sl.output("分离文件读取源目录：{0}", source);
        final List<String> files = Ut.ioFiles(source);
        Sl.output("即将合并文件数：{0}", String.valueOf(files.size()));
        /*
         * 完整数据
         */
        final JsonObject data = new JsonObject();
        files.forEach(file -> {
            final String filePath = source + "/" + file;
            final JsonObject fileData = Ut.ioJObject(filePath);
            if (Ut.isNotNil(fileData)) {
                data.put(file.replace(".json", ""), fileData);
            } else {
                Sl.output("忽略文件：{0}", filePath);
            }
        });

        /*
         * 双路径写入
         */
        final String target = this.path("runtime/");
        String targetFile = target + "external/mapping.json";
        Sl.output("写入第一路径：{0}", targetFile);
        Ut.ioOut(targetFile, data);

        /*
         * 额外路径信息
         */
        targetFile = target + "cmdb-v2/mapping/ucmdb.json";
        Sl.output("写入第二路径：{0}", targetFile);
        Ut.ioOut(targetFile, data);
        return EmCommand.TermStatus.SUCCESS;
    }
}
