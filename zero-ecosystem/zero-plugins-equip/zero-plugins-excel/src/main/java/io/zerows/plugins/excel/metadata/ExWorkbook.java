package io.zerows.plugins.excel.metadata;

import io.zerows.platform.constant.VString;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.Serializable;

/**
 * @author lang : 2024-06-12
 */
public class ExWorkbook implements Serializable {
    private final Workbook workbook;
    private String filename;
    private String directory;

    public ExWorkbook(final Workbook workbook) {
        this.workbook = workbook;
    }

    public Workbook getWorkbook() {
        return this.workbook;
    }

    public String getDirectory() {
        return this.directory;
    }

    public String getFilename() {
        return this.filename;
    }

    public ExWorkbook bind(final String filename) {
        this.filename = filename;
        /*
         * 此处的 directory 主要针对文件名进行替换，将文件名后缀拿掉之后，转换成目录名称
         * 所有后续的部分操作都和 directory 有关，这里为新规则，新规则支持部分特殊单元格模式
         * CODE:class
         * CODE:config
         * NAME:config
         * CODE:NAME:config
         * PWD
         */
        this.directory = filename.substring(0, filename.lastIndexOf(VString.DOT));
        return this;
    }
}
