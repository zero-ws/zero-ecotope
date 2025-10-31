package io.zerows.plugins.excel;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.zerows.plugins.excel.metadata.ExTable;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.modeling.metadata.HMetaAtom;

import java.io.InputStream;
import java.util.Set;

/**
 * ExcelClient for office excel data loading
 * Apache Poi
 */
@AddOn.Name("DEFAULT_EXCEL_CLIENT")
public interface ExcelClient {

    static ExcelClient createClient(final Vertx vertx, final HConfig config) {
        return new ExcelClientImpl(vertx, config);
    }

    // --------------------- ExTable Ingesting -----------------------

    Future<Set<ExTable>> ingestAsync(String filename);

    Future<Set<ExTable>> ingestAsync(String filename, HMetaAtom metaAtom);

    Future<Set<ExTable>> ingestAsync(InputStream in, boolean isXlsx);

    Future<Set<ExTable>> ingestAsync(InputStream in, boolean isXlsx, HMetaAtom metaAtom);

    Set<ExTable> ingest(String filename);

    Set<ExTable> ingest(String filename, HMetaAtom metaAtom);

    Set<ExTable> ingest(InputStream in, boolean isXlsx);

    Set<ExTable> ingest(InputStream in, boolean isXlsx, HMetaAtom metaAtom);

    @Fluent
    ExcelClient ingest(String filename, Handler<AsyncResult<Set<ExTable>>> handler);


    // --------------------- ExTable Exporting -----------------------

    @Fluent
    ExcelClient ingest(String filename, HMetaAtom metaAtom, Handler<AsyncResult<Set<ExTable>>> handler);

    @Fluent
    ExcelClient ingest(InputStream in, boolean isXlsx, Handler<AsyncResult<Set<ExTable>>> handler);

    @Fluent
    ExcelClient ingest(InputStream in, boolean isXlsx, HMetaAtom metaAtom, Handler<AsyncResult<Set<ExTable>>> handler);

    Future<Buffer> exportAsync(String identifier, JsonArray data);

    // --------------------- ExTable Loading / Importing -----------------------

    Future<Buffer> exportAsync(String identifier, JsonArray data, HMetaAtom metaAtom);

    @Fluent
    ExcelClient exportAsync(String identifier, JsonArray data, Handler<AsyncResult<Buffer>> handler);

    @Fluent
    ExcelClient exportAsync(String identifier, JsonArray data, HMetaAtom metaAtom, Handler<AsyncResult<Buffer>> handler);

    @Fluent
    @CanIgnoreReturnValue
    <T> ExcelClient importAsync(String filename, Handler<AsyncResult<Set<T>>> handler);

    @Fluent
    <T> ExcelClient importAsync(String filename, HMetaAtom metaAtom, Handler<AsyncResult<Set<T>>> handler);

    @Fluent
    <T> ExcelClient importAsync(InputStream in, boolean isXlsx, Handler<AsyncResult<Set<T>>> handler);

    @Fluent
    <T> ExcelClient importAsync(InputStream in, boolean isXlsx, HMetaAtom metaAtom, Handler<AsyncResult<Set<T>>> handler);

    <T> Future<Set<T>> importAsync(String filename);

    <T> Future<Set<T>> importAsync(String filename, HMetaAtom metaAtom);

    <T> Future<Set<T>> importAsync(InputStream in, boolean isXlsx);

    <T> Future<Set<T>> importAsync(InputStream in, boolean isXlsx, HMetaAtom metaAtom);

    /*
     * Filtered by `includes`
     */
    @Fluent
    <T> ExcelClient importAsync(String filename, Handler<AsyncResult<Set<T>>> handler, String... includes);

    @Fluent
    <T> ExcelClient importAsync(String filename, HMetaAtom metaAtom, Handler<AsyncResult<Set<T>>> handler, String... includes);

    @Fluent
    <T> ExcelClient importAsync(InputStream in, boolean isXlsx, Handler<AsyncResult<Set<T>>> handler, String... includes);

    @Fluent
    <T> ExcelClient importAsync(InputStream in, boolean isXlsx, HMetaAtom metaAtom, Handler<AsyncResult<Set<T>>> handler, String... includes);

    <T> Future<Set<T>> importAsync(String filename, String... includes);

    <T> Future<Set<T>> importAsync(String filename, HMetaAtom metaAtom, String... includes);

    <T> Future<Set<T>> importAsync(InputStream in, boolean isXlsx, String... includes);

    <T> Future<Set<T>> importAsync(InputStream in, boolean isXlsx, HMetaAtom metaAtom, String... includes);

    Future<JsonArray> extractAsync(final ExTable table);

    Future<JsonArray> extractAsync(final Set<ExTable> tables);
}
