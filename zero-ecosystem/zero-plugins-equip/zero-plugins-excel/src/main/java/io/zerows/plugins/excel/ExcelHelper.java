package io.zerows.plugins.excel;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.jigsaw.Oneness;
import io.zerows.platform.constant.VValue;
import io.zerows.plugins.excel.component.DataTaker;
import io.zerows.plugins.excel.component.ExBound;
import io.zerows.plugins.excel.component.ExBoundRow;
import io.zerows.plugins.excel.component.ExExpr;
import io.zerows.plugins.excel.component.ExcelEnv;
import io.zerows.plugins.excel.component.ExcelEnvConnect;
import io.zerows.plugins.excel.component.ExcelEnvFormula;
import io.zerows.plugins.excel.component.ExcelEnvPen;
import io.zerows.plugins.excel.component.ExcelEnvTenant;
import io.zerows.plugins.excel.exception._60037Exception404ExcelFileNull;
import io.zerows.plugins.excel.metadata.ExRecord;
import io.zerows.plugins.excel.metadata.ExTable;
import io.zerows.plugins.excel.metadata.ExTenant;
import io.zerows.plugins.excel.metadata.ExWorkbook;
import io.zerows.plugins.excel.style.ExTpl;
import io.zerows.plugins.excel.util.ExDataApply;
import io.zerows.specification.modeling.metadata.HMetaAtom;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Excel Helper to help ExcelClient to do some object building
 */
class ExcelHelper {

    private static final Cc<String, ExcelHelper> CC_HELPER = Cc.open();
    private static final Cc<String, Workbook> CC_WORKBOOK = Cc.open();
    private static final Cc<Integer, Workbook> CC_WORKBOOK_STREAM = Cc.open();
    // 构造的特殊对象
    private static final Map<String, Workbook> REFERENCES = new ConcurrentHashMap<>();
    private transient final Class<?> target;

    // 四种特殊对象对应的构造器
    private final transient ExcelEnv<MDConfiguration> envConnect;
    private final transient ExcelEnv<ExTpl> envPen;
    private final transient ExcelEnv<Map<String, Workbook>> envFormula;
    private final transient ExcelEnv<ExTenant> envTenant;
    // 构造的特殊对象
    private transient ExTpl tpl;
    private transient ExTenant tenant;

    @SuppressWarnings("unchecked")
    private ExcelHelper(final Class<?> target) {
        this.target = target;

        this.envConnect = (ExcelEnv<MDConfiguration>) ExcelEnv.of(ExcelEnvConnect.class);
        this.envPen = (ExcelEnv<ExTpl>) ExcelEnv.of(ExcelEnvPen.class);
        this.envFormula = ((ExcelEnvFormula) ExcelEnv.of(ExcelEnvFormula.class))
            .bind(this::getWorkbook);
        this.envTenant = (ExcelEnv<ExTenant>) ExcelEnv.of(ExcelEnvTenant.class);
    }

    static ExcelHelper helper(final Class<?> target) {
        return CC_HELPER.pick(() -> new ExcelHelper(target), target.getName());
        // FnZero.po?l(Pool.HELPERS, ofMain.getName(), () -> new ExcelHelper(ofMain));
    }

    Future<JsonArray> extract(final Set<ExTable> tables) {
        final List<Future<JsonArray>> futures = new ArrayList<>();
        tables.forEach(table -> futures.add(this.extract(table)));
        return Fx.compressA(futures);
    }

    Future<JsonArray> extract(final ExTable table) {
        /* Records extracting */
        final List<ExRecord> records = table.get();
        final String tableName = table.getName();
        /* Pojo Processing */
        final JsonArray dataArray = new JsonArray();
        records.stream().filter(Objects::nonNull)
            // 解析表格中的语法格式处理
            .map(ExExpr.of()::parse)
            .forEach(dataArray::add);

        /* dictionary for static part */
        return DataTaker.ofStatic(this.tenant).extract(dataArray, tableName)
            /* dictionary for dynamic part */
            .compose(extracted -> DataTaker.ofDynamic(this.tenant).extract(extracted, tableName))
            /* forbidden record filter */
            .compose(extracted -> DataTaker.ofForbidden(this.tenant).extract(extracted, tableName));
    }

    /*
     * Read file from path to web Excel Workbook object.
     * If this function findRunning null dot file or file object, zero system
     * will throw exception out.
     */
    @SuppressWarnings("all")
    Workbook getWorkbook(final String filename) {
        Fn.jvmKo(Objects.isNull(filename), _60037Exception404ExcelFileNull.class, filename);
        /*
         * Here the InputStream directly from
         */
        final InputStream in = Ut.ioStream(filename, getClass());
        Fn.jvmKo(Objects.isNull(in), _60037Exception404ExcelFileNull.class, filename);
        final Workbook workbook;
        if (filename.endsWith(VValue.SUFFIX.EXCEL_2003)) {
            workbook = CC_WORKBOOK.pick(() -> Fn.jvmOr(() -> new HSSFWorkbook(in)), filename);
            // FnZero.po?l(Pool.WORKBOOKS, filename, () -> FnZero.getJvm(() -> new HSSFWorkbook(in)));
        } else {
            workbook = CC_WORKBOOK.pick(() -> Fn.jvmOr(() -> new XSSFWorkbook(in)), filename);
            // FnZero.po?l(Pool.WORKBOOKS, filename, () -> FnZero.getJvm(() -> new XSSFWorkbook(in)));
        }
        return workbook;
    }

    @SuppressWarnings("all")
    Workbook getWorkbook(final InputStream in, final boolean isXlsx) {
        Fn.jvmKo(Objects.isNull(in), _60037Exception404ExcelFileNull.class, "Stream");
        final Workbook workbook;
        if (isXlsx) {
            workbook = CC_WORKBOOK_STREAM.pick(() -> Fn.jvmOr(() -> new XSSFWorkbook(in)), in.hashCode());
            // FnZero.po?l(Pool.WORKBOOKS_STREAM, in.hashCode(), () -> FnZero.getJvm(() -> new XSSFWorkbook(in)));
        } else {
            workbook = CC_WORKBOOK_STREAM.pick(() -> Fn.jvmOr(() -> new HSSFWorkbook(in)), in.hashCode());
            // FnZero.po?l(Pool.WORKBOOKS_STREAM, in.hashCode(), () -> FnZero.getJvm(() -> new HSSFWorkbook(in)));
        }
        /* Force to recalculation for evaluator */
        workbook.setForceFormulaRecalculation(Boolean.TRUE);
        return workbook;
    }

    /*
     * Get Set<ExSheet> collection based join workbook
     */
    Set<ExTable> getExTables(final ExWorkbook exWorkbook, final HMetaAtom metaAtom) {
        if (Objects.isNull(exWorkbook) || Objects.isNull(exWorkbook.getWorkbook())) {
            return new HashSet<>();
        }
        final Workbook workbook = exWorkbook.getWorkbook();
        /* FormulaEvaluator reference */
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        /*
         * Workbook pool for FormulaEvaluator
         * 1）Local variable to replace global
         **/
        final Map<String, FormulaEvaluator> references = new ConcurrentHashMap<>();
        REFERENCES.forEach((field, workbookRef) -> {
            /*
             * Reference executor processing
             * Here you must put self reference evaluator and all related here.
             * It should fix issue: Could not set environment etc.
             */
            final FormulaEvaluator executorRef = workbookRef.getCreationHelper().createFormulaEvaluator();
            references.put(field, executorRef);
        });
        /*
         * Self evaluator for current calculation
         */
        references.put(workbook.createName().getNameName(), evaluator);

        /*
         * Above one line code resolved following issue:
         * org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment$WorkbookNotFoundException:
         * Could not resolve external workbook name 'environment.ambient.xlsx'. Workbook environment has not been set up.
         */
        evaluator.setupReferencedWorkbooks(references);
        /*
         * Sheet process
         */
        final Iterator<Sheet> it = workbook.sheetIterator();
        final Set<ExTable> sheets = new HashSet<>();
        while (it.hasNext()) {
            /* Build temp ExSheet */
            final Sheet sheet = it.next();
            /* Build Range ( Row Start - End ) */
            final ExBound range = new ExBoundRow(sheet);

            final ExcelAnalyzer exSheet = new ExcelAnalyzer(sheet).on(evaluator);
            /* Build Set */
            final Set<ExTable> dataSet = exSheet.analyzed(range, metaAtom);
            /* Bind current directory for ExTable to support expression cell syntax */
            dataSet.forEach(table -> table.setDirectory(exWorkbook.getDirectory()));


            final ExDataApply apply = ExDataApply.of(this.tenant);
            apply.applyData(dataSet);


            sheets.addAll(dataSet);
        }
        return sheets;
    }

    void runBrush(final Workbook workbook, final Sheet sheet, final HMetaAtom metaAtom) {
        if (Objects.nonNull(this.tpl)) {
            this.tpl.bind(workbook);
            this.tpl.applyStyle(sheet, metaAtom);
        }
    }

    // --------------------- 初始化专用方法 ---------------------
    void initPen(final JsonObject excelJ) {
        this.tpl = this.envPen.prepare(excelJ);
    }

    void initConnect(final JsonObject excelJ) {
        this.envConnect.prepare(excelJ);
    }

    void initEnvironment(final JsonObject excelJ) {
        REFERENCES.putAll(this.envFormula.prepare(excelJ));
    }

    void initTenant(final JsonObject excelJ) {
        this.tenant = this.envTenant.prepare(excelJ);
    }

    /*
     * For Insert to avoid duplicated situation
     * 1. Key duplicated
     * 2. Unique duplicated
     */
    <T> List<T> compress(final List<T> input, final ExTable table) {

        final MDConnect connect = table.getConnect();
        final Oneness<MDConnect> oneness = Oneness.ofConnect();
        final String keyPrimary = oneness.keyPrimary(connect);

        if (Objects.isNull(keyPrimary)) {
            // Relation Table
            return input;
        }
        final List<T> keyList = new ArrayList<>();
        final Set<Object> keys = new HashSet<>();
        final AtomicInteger counter = new AtomicInteger(0);
        input.forEach(item -> {
            final Object value = Ut.field(item, keyPrimary);
            if (Objects.nonNull(value) && !keys.contains(value)) {
                keys.add(value);
                keyList.add(item);
            } else {
                counter.incrementAndGet();
            }
        });
        final int ignored = counter.get();
        if (0 < ignored) {
            final LogOf annal = LogOf.get(this.target);
            annal.warn("[ Έξοδος ] Ignore table `{0}` with size `{1}`", table.getName(), ignored);
        }
        // Entity Release
        return keyList;
    }
}
