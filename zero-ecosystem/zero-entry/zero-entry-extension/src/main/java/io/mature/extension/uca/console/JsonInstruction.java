package io.mature.extension.uca.console;

import io.mature.extension.scaffold.console.AbstractInstruction;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.atom.Schema;
import io.zerows.extension.mbse.basement.uca.file.AoFile;
import io.zerows.extension.mbse.basement.uca.file.ExcelReader;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.unity.Ux;

import java.util.HashSet;
import java.util.Set;

import static io.mature.extension.refine.Ox.LOG;

/**
 * （专用建模工具）
 * Excel 建模文件转换成 InJson 的建模文件
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class JsonInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput input) {
        final String appName = this.inString(input, "a");
        /*
         * appName为null，直接获取app
         */
        if (appName == null) {
            return this.partyA().compose(ok -> this.defaultValue(input, ok.configArk()));
        } else {
            return this.partyB(appName).compose(okB -> this.defaultValue(input, okB.configArk()));
        }
    }

    private Future<EmCommand.TermStatus> defaultValue(final CommandInput input, final HArk ark) {
        final String inputPath = this.inString(input, "i");
        final String outPath = this.inString(input, "o");

        final AoFile reader = Ut.singleton(ExcelReader.class, inputPath);

        /*
         * 模型文件写入
         */
        final HApp app = ark.app();
        final Set<Model> models = reader.readModels(app.name());
        final Set<Schema> schemata = new HashSet<>();
        models.forEach(model -> {
            final JsonObject modelJson = model.toJson();
            final String resolved = this.outPath(outPath + "model", model.identifier());
            LOG.Hub.info(this.logger(), "写入模型（Model）：{0} -> {1}", model.identifier(), resolved);
            Ut.ioOut(resolved, modelJson);

            schemata.addAll(model.schema());
        });
        /*
         * 实体文件写入
         */
        schemata.forEach(schema -> {
            final JsonObject schemaJson = schema.toJson();
            final String resolved = this.outPath(outPath + "schema", schema.identifier());
            LOG.Hub.info(this.logger(), "写入实体（Schema）：{0} -> {1}", schema.identifier(), resolved);
            Ut.ioOut(resolved, schemaJson);
        });
        return Ux.future(EmCommand.TermStatus.SUCCESS);
    }

    private String outPath(final String folder, final String identifier) {
        final String absolutePath = this.inFolder(this.path(folder));
        return absolutePath + "/" + identifier + VString.DOT + VPath.SUFFIX.JSON;
    }
}
