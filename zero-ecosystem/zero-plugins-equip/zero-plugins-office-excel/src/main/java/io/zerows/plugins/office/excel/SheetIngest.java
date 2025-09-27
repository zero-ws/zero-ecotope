package io.zerows.plugins.office.excel;

import io.zerows.specification.modeling.metadata.HMetaAtom;
import io.vertx.core.Future;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.plugins.office.excel.atom.ExWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class SheetIngest {
    private transient final ExcelHelper helper;

    private SheetIngest(final ExcelHelper helper) {
        this.helper = helper;
    }

    static SheetIngest create(final ExcelHelper helper) {
        return new SheetIngest(helper);
    }

    /*
     * 1. Get Workbook reference
     * 2. Iterator for Sheet ( By Analyzer )
     */
    Set<ExTable> ingest(final InputStream in, final boolean isXlsx) {
        return this.ingest(in, isXlsx, HMetaAtom.of());
    }

    Set<ExTable> ingest(final String filename) {
        return this.ingest(filename, HMetaAtom.of());
    }

    /**
     * 这种模式读取表格不支持文件名模式，即这种模式通常只用于上传下载流程，不支持使用当前文件目录名作为配置数据的表格名称，所以这种模式下所有
     * 解析类的表格数据都是不支持的，如 {UUID}、CODE:class、CODE:config、NAME:config、CODE:NAME:config、PWD 等，由于本身不带有目录
     * 信息，无法单纯通过 {@link InputStream} 计算目录，所以这样的模式下不支持目录计算，自然不支持解析类表格。
     *
     * @param in       输入流
     * @param isXlsx   Excel 格式切换
     * @param metaAtom 元模型定义
     *
     * @return 加载的表格数据
     */
    Set<ExTable> ingest(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom) {
        final Workbook workbook = this.helper.getWorkbook(in, isXlsx);
        final ExWorkbook exWorkbook = new ExWorkbook(workbook);
        return this.helper.getExTables(exWorkbook, metaAtom);
    }

    Set<ExTable> ingest(final String filename, final HMetaAtom metaAtom) {
        final Workbook workbook = this.helper.getWorkbook(filename);
        final ExWorkbook exWorkbook = new ExWorkbook(workbook).bind(filename);
        return this.helper.getExTables(exWorkbook, metaAtom);
    }

    private Set<ExTable> compress(final Set<ExTable> processed, final String... includes) {
        final Set<String> tables = new HashSet<>(Arrays.asList(includes));
        return processed.stream()
            .filter(table -> tables.contains(table.getName()))
            .collect(Collectors.toSet());
    }

    Future<Set<ExTable>> compressAsync(final Set<ExTable> processed, final String... includes) {
        return Future.succeededFuture(this.compress(processed, includes));
    }
}
