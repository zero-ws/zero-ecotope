package io.zerows.plugins.excel;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.excel.metadata.ExTable;
import io.zerows.specification.modeling.metadata.HMetaAtom;

import java.io.InputStream;
import java.util.Set;

public class ExcelClientImpl implements ExcelClient {

    private transient final Vertx vertx;
    private transient final ExcelHelper helper = ExcelHelper.helper(this.getClass());
    // Excel Exporter
    private transient final ExcelExport exporter = ExcelExport.create(this.helper);
    private transient final ExcelIngest ingest = ExcelIngest.create(this.helper);
    private transient final ExcelImport importer = ExcelImport.create(this.helper);

    ExcelClientImpl(final Vertx vertx, final JsonObject config) {
        this.vertx = vertx;
        this.init(config);
    }

    @Override
    public ExcelClient init(final JsonObject config) {
        this.helper.initConnect(config);


        this.helper.initEnvironment(config);


        this.helper.initPen(config);


        this.helper.initTenant(config);
        return this;
    }

    // --------------------- ExTable Ingesting -----------------------

    /**
     * 1) Api for ingest of parameters, the matrix is as following
     * |    filename/input-stream   |    2003/2007                       |     Shape     |      Async       |
     * |        filename            |    by extension ( .xls, .xlsx )    |       x       |      false       |
     * |        input-stream        |    isXlsx, true for 2007           |       x       |      false       |
     * |        filename            |    by extension ( .xls, .xlsx )    |       V       |      false       |
     * |        input-stream        |    isXlsx, true for 2007           |       V       |      false       |
     * |        filename            |    by extension ( .xls, .xlsx )    |       x       |      true        |
     * |        input-stream        |    isXlsx, true for 2007           |       x       |      true        |
     * |        filename            |    by extension ( .xls, .xlsx )    |       V       |      true        |
     * |        input-stream        |    isXlsx, true for 2007           |       V       |      true        |
     * 2) 12 mode of `ingest` here
     * 2.1) The input contains two categories:
     * -- 1. InputStream for byte array input, in this mode, you must provide `isXlsx` parameter
     * -- 2. filename of input, the formatFail should be distinguish by file extension `.xls` for 2003, `.xlsx` for 2007
     * 2.2) The Shape contains `Dynamic` importing in Ox channel or other web `Shape` object, it contains type definition
     * 2.2) async contains ( Sync, Callback, Future ) three mode
     */
    @Override
    public Set<ExTable> ingest(final String filename) {
        return this.ingest.ingest(filename);
    }

    @Override
    public Set<ExTable> ingest(final String filename, final HMetaAtom metaAtom) {
        return this.ingest.ingest(filename, metaAtom);
    }

    @Override
    public Set<ExTable> ingest(final InputStream in, final boolean isXlsx) {
        return this.ingest.ingest(in, isXlsx);
    }

    @Override
    public Set<ExTable> ingest(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom) {
        return this.ingest.ingest(in, isXlsx, metaAtom);
    }

    @Override
    public Future<Set<ExTable>> ingestAsync(final String filename) {
        return Future.succeededFuture(this.ingest(filename));
    }

    @Override
    public Future<Set<ExTable>> ingestAsync(final String filename, final HMetaAtom metaAtom) {
        return Future.succeededFuture(this.ingest(filename, metaAtom));
    }

    @Override
    public Future<Set<ExTable>> ingestAsync(final InputStream in, final boolean isXlsx) {
        return Future.succeededFuture(this.ingest(in, isXlsx));
    }

    @Override
    public Future<Set<ExTable>> ingestAsync(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom) {
        return Future.succeededFuture(this.ingest(in, isXlsx, metaAtom));
    }

    @Override
    @Fluent
    public ExcelClient ingest(final String filename, final Handler<AsyncResult<Set<ExTable>>> handler) {
        handler.handle(this.ingestAsync(filename));
        return this;
    }

    @Override
    @Fluent
    public ExcelClient ingest(final String filename, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<ExTable>>> handler) {
        handler.handle(this.ingestAsync(filename, metaAtom));
        return this;
    }

    @Override
    @Fluent
    public ExcelClient ingest(final InputStream in, final boolean isXlsx, final Handler<AsyncResult<Set<ExTable>>> handler) {
        handler.handle(this.ingestAsync(in, isXlsx));
        return this;
    }

    @Override
    @Fluent
    public ExcelClient ingest(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<ExTable>>> handler) {
        handler.handle(this.ingestAsync(in, isXlsx, metaAtom));
        return this;
    }

    // --------------------- ExTable Loading / Importing -----------------------

    /**
     * 1) Api for ingest of parameters, the matrix is as following
     * |    filename/input-stream   |    2003/2007                       |     Shape     |      Async       |
     * |        filename            |    by extension ( .xls, .xlsx )    |       x       |      true        |
     * |        input-stream        |    isXlsx, true for 2007           |       x       |      true        |
     * |        filename            |    by extension ( .xls, .xlsx )    |       V       |      true        |
     * |        input-stream        |    isXlsx, true for 2007           |       V       |      true        |
     *
     * 2) 12 mode of `ingest` here
     * 2.1) The input contains two categories:
     * -- 1. InputStream for byte array input, in this mode, you must provide `isXlsx` parameter
     * -- 2. filename of input, the formatFail should be distinguish by file extension `.xls` for 2003, `.xlsx` for 2007
     * 2.2) The Shape contains `Dynamic` importing in Ox channel or other web `Shape` object, it contains type definition
     * 2.2) async contains ( Sync, Callback, Future ) three mode
     */
    @Override
    @Fluent
    public <T> ExcelClient importAsync(final String filename, final Handler<AsyncResult<Set<T>>> handler) {
        return this.ingest(filename, res -> handler.handle(this.importer.importAsync(res)));
    }

    @Override
    @Fluent
    public <T> ExcelClient importAsync(final InputStream in, final boolean isXlsx, final Handler<AsyncResult<Set<T>>> handler) {
        return this.ingest(in, isXlsx, res -> handler.handle(this.importer.importAsync(res)));
    }

    @Override
    public <T> ExcelClient importAsync(final String filename, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<T>>> handler) {
        return this.ingest(filename, metaAtom, res -> handler.handle(this.importer.importAsync(res)));
    }

    @Override
    public <T> ExcelClient importAsync(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<T>>> handler) {
        return this.ingest(in, isXlsx, metaAtom, res -> handler.handle(this.importer.importAsync(res)));
    }

    @Override
    public <T> Future<Set<T>> importAsync(final String filename) {
        return this.ingestAsync(filename).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final String filename, final HMetaAtom metaAtom) {
        return this.ingestAsync(filename, metaAtom).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final InputStream in, final boolean isXlsx) {
        return this.ingestAsync(in, isXlsx).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom) {
        return this.ingestAsync(in, isXlsx, metaAtom).compose(this.importer::importAsync);
    }

    @Override
    public <T> ExcelClient importAsync(final String filename, final Handler<AsyncResult<Set<T>>> handler, final String... includes) {
        return this.ingest(filename, res -> handler.handle(this.importer.importAsync(this.ingest.compressAsync(res.result(), includes))));
    }

    @Override
    public <T> ExcelClient importAsync(final String filename, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<T>>> handler, final String... includes) {
        return this.ingest(filename, metaAtom, res -> handler.handle(this.importer.importAsync(this.ingest.compressAsync(res.result(), includes))));
    }

    @Override
    public <T> ExcelClient importAsync(final InputStream in, final boolean isXlsx, final Handler<AsyncResult<Set<T>>> handler, final String... includes) {
        return this.ingest(in, isXlsx, res -> handler.handle(this.importer.importAsync(this.ingest.compressAsync(res.result(), includes))));
    }

    @Override
    public <T> ExcelClient importAsync(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom, final Handler<AsyncResult<Set<T>>> handler, final String... includes) {
        return this.ingest(in, isXlsx, metaAtom, res -> handler.handle(this.importer.importAsync(this.ingest.compressAsync(res.result(), includes))));
    }

    @Override
    public <T> Future<Set<T>> importAsync(final String filename, final String... includes) {
        return this.ingestAsync(filename).compose(tables -> this.ingest.compressAsync(tables, includes)).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final String filename, final HMetaAtom metaAtom, final String... includes) {
        return this.ingestAsync(filename, metaAtom).compose(tables -> this.ingest.compressAsync(tables, includes)).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final InputStream in, final boolean isXlsx, final String... includes) {
        return this.ingestAsync(in, isXlsx).compose(tables -> this.ingest.compressAsync(tables, includes)).compose(this.importer::importAsync);
    }

    @Override
    public <T> Future<Set<T>> importAsync(final InputStream in, final boolean isXlsx, final HMetaAtom metaAtom, final String... includes) {
        return this.ingestAsync(in, isXlsx, metaAtom).compose(tables -> this.ingest.compressAsync(tables, includes)).compose(this.importer::importAsync);
    }

    // --------------------- ExTable Exporting -----------------------
    @Override
    public ExcelClient exportAsync(final String identifier, final JsonArray data, final Handler<AsyncResult<Buffer>> handler) {
        this.exporter.exportData(identifier, data, HMetaAtom.of(), handler);
        return this;
    }

    @Override
    public ExcelClient exportAsync(final String identifier, final JsonArray data,
                                   final HMetaAtom metaAtom, final Handler<AsyncResult<Buffer>> handler) {
        this.exporter.exportData(identifier, data, metaAtom, handler);
        return this;
    }

    @Override
    public Future<Buffer> exportAsync(final String identifier, final JsonArray data,
                                      final HMetaAtom metaAtom) {
        return this.exporter.exportData(identifier, data, metaAtom);
    }

    @Override
    public Future<Buffer> exportAsync(final String identifier, final JsonArray data) {
        return this.exporter.exportData(identifier, data, HMetaAtom.of());
    }

    // --------------------- Spec Workflow -----------------------
    @Override
    public Future<JsonArray> extractAsync(final ExTable table) {
        return this.helper.extract(table);
    }

    @Override
    public Future<JsonArray> extractAsync(final Set<ExTable> tables) {
        return this.helper.extract(tables);
    }
}
