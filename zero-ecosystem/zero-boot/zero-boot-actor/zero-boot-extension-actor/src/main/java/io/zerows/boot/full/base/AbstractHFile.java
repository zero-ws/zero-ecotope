package io.zerows.boot.full.base;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.metadata.Apt;
import io.zerows.extension.module.mbsecore.exception._80526Exception400FileRequired;
import io.zerows.extension.module.mbsecore.exception._80531Exception409IdentifierConflict;
import io.zerows.extension.module.mbsecore.exception._80532Exception404DataEmpty;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.mbse.metadata.ActIn;
import io.zerows.mbse.metadata.ActOut;
import io.zerows.plugins.excel.ExcelActor;
import io.zerows.plugins.excel.ExcelClient;
import io.zerows.plugins.excel.metadata.ExRecord;
import io.zerows.plugins.excel.metadata.ExTable;
import io.zerows.program.Ux;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import static io.zerows.boot.extension.util.Ox.LOG;

/**
 * ## 导入专用顶层通道
 *
 * ### 1. 基本介绍
 *
 * 该通道为文件上传专用通道
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractHFile extends AbstractHMore {
    /**
     * 「批量」重写上传的`transferIn`来完成
     *
     * 1. 单文件读取
     * 2. 文件读取解析`ExTable`导入表格
     * 3. 读取表格相关数据（包括字典预处理）
     * 4. 翻译后按条件查询
     *
     * @param request {@link ActIn} 输入业务请求
     *
     * @return {@link io.vertx.core.Future}<{@link ActOut}> 业务层输出
     */
    @Override
    public Future<Apt> transferIn(final ActIn request) {
        /*
         * 导入专用
         */
        final File[] files = request.getFiles();
        if (0 >= files.length) {
            return FnVertx.failOut(_80526Exception400FileRequired.class);
        }


        final File file = files[0];
        try {
            final InputStream inputStream = new FileInputStream(file);
            final ExcelClient client = ExcelActor.ofClient();
            /*
             * 批量插入收集到的数据
             */
            final DataAtom atom = this.atom();
            final String identifier = atom.identifier();
            /*
             * Set<ExTable>
             */
            final Set<ExTable> tables = client.ingest(inputStream, true, atom.shape());
            /*
             * 表检查
             */
            final long counter = tables.stream().filter(table -> !table.getName().equals(identifier)).count();
            if (0 < counter) {
                LOG.Uca.warn(this.getClass(), "文件规范错误，期望 identifier = {0}", identifier);
                return FnVertx.failOut(_80531Exception409IdentifierConflict.class, identifier);
            }


            final JsonArray data = new JsonArray();
            tables.stream().filter(table -> identifier.equals(table.getName()))
                .map(ExTable::get)
                .flatMap(Collection::stream)
                .map(ExRecord::toJson)
                .forEach(data::add);
            /*
             * 字典翻译
             */
            if (data.isEmpty()) {
                return FnVertx.failOut(_80532Exception404DataEmpty.class, this.getClass());
            }


            final JsonArray current = this.fabric.inFromS(data);
            return this.transferIn(current)
                /*
                 * 按配置项的 Code 查询
                 */
                .compose(original -> Ux.future(Apt.create(original, current, this.diffKey())));
        } catch (final Throwable ex) {
            return Future.failedFuture(ex);
        }
    }

    /**
     * 旧数据直接读取，重载方法`transferIn`方法。
     *
     * @param current {@link JsonArray} 当前数据
     *
     * @return {@link io.vertx.core.Future}<{@link JsonArray}>
     */
    public abstract Future<JsonArray> transferIn(JsonArray current);
}
