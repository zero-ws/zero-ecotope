package io.zerows.common.datamation;

import io.zerows.core.uca.log.Annal;
import io.zerows.ams.util.HUt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「完整字典对象」此对象定义了完整的字典对象，其中包括
 * <pre><code>
 *     1. 字典存储器
 *     2. 字典源定义
 *     3. 字典消费定义
 *     4. 字典翻译器
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KFabric {

    private static final Annal LOGGER = Annal.get(KFabric.class);
    private final ConcurrentMap<String, KDictUse> epsilonMap
        = new ConcurrentHashMap<>();
    /*
     * Each fabric bind
     */
    private final KDictData store = new KDictData();
    /*
     *  The mapping in dictionary
     */
    private final KMapping mapping;

    /*
     * Data here for dictionary
     */
    private final ConcurrentMap<String, KMapping> fromData
        = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, KMapping> toData
        = new ConcurrentHashMap<>();

    private KFabric(final KMapping mapping) {
        this.mapping = mapping;
    }

    /*
     * Here are the creation method for `DictFabric`
     * Each api will create new `DictFabric` object
     */
    public static KFabric create(final KMapping mapping) {
        return new KFabric(mapping);
    }

    public static KFabric create() {
        return new KFabric(null);
    }

    public KFabric copy() {
        return this.copy(null);
    }

    public KFabric copy(final KMapping mapping) {
        /*
         * Here are two mapping for copy
         * 1. When `mapping` is null, check whether there exist mapping
         * 2. When `mapping` is not null, the mapping will be overwrite directly
         *
         * Fix issue of : java.lang.NullPointerException
         * when you call `createCopy()` directly.
         */
        final KMapping calculated = Objects.isNull(mapping) ? this.mapping : mapping;
        final KFabric created = create(calculated);
        created.dictionary(this.store.data());
        created.epsilon(this.epsilonMap);
        return created;
    }

    public KFabric epsilon(final ConcurrentMap<String, KDictUse> epsilonMap) {
        if (Objects.nonNull(epsilonMap) && !epsilonMap.isEmpty()) {
            /*
             * Re-bind
             */
            this.epsilonMap.clear();                        /* Clear Queue */
            epsilonMap.forEach((key, epsilon) -> {
                /*
                 * Only pick up valid configured `epsilon`
                 * Other invalid will be ignored.
                 */
                if (epsilon.isValid()) {
                    this.epsilonMap.put(key, epsilon);
                }
            });
        } else {
            LOGGER.debug("DictFabric got empty epsilonMap ( ConcurrentMap<String, DictEpsilon> ) !");
        }
        this.init();
        return this;
    }

    public KFabric dictionary(final ConcurrentMap<String, JsonArray> dictData) {
        // Call get for data replaced
        this.store.data(dictData);
        this.init();
        return this;
    }

    /*
     * The stored data that related to configuration defined here
     */
    public KMapping mapping() {
        return this.mapping;
    }

    public ConcurrentMap<String, KDictUse> epsilon() {
        return this.epsilonMap;
    }

    public ConcurrentMap<String, JsonArray> dictionary() {
        return this.store.data();
    }

    public JsonArray dictionary(final String dictName) {
        return this.store.item(dictName);
    }

    private void init() {
        if (this.ready()) {
            /*
             * Iterate the epsilonMap
             */
            this.epsilonMap.forEach((fromField, epsilon) -> {
                /*
                 * Get JsonArray from dict
                 */
                final JsonArray dataArray = this.store.item(epsilon.getSource());
                /*
                 * Data Source is dataArray
                 * Build current `DualItem`
                 */
                final JsonObject dataItem = new JsonObject();
                HUt.itJArray(dataArray).forEach(item -> {
                    /*
                     * Data in ( From ) - out ( To )
                     */
                    final String inValue = item.getString(epsilon.getIn());
                    final String outValue = item.getString(epsilon.getOut());
                    if (HUt.isNotNil(inValue) && HUt.isNotNil(outValue)) {
                        dataItem.put(inValue, outValue);
                    }
                });
                /*
                 * Fill data in our data structure
                 */
                if (HUt.isNotNil(dataItem)) {

                    /*
                     * From Data Map processing
                     */
                    final KMapping item = new KMapping(dataItem);
                    this.fromData.put(fromField, item);

                    /*
                     * To Data Map processing
                     */
                    if (Objects.nonNull(this.mapping)) {
                        final String hitField = this.mapping.to(fromField);
                        if (HUt.isNotNil(hitField)) {
                            this.toData.put(hitField, item);
                        }
                    }
                }
            });
        }
    }

    private boolean ready() {
        return !this.epsilonMap.isEmpty() && this.store.ready();
    }

    /*
     * DualItem ->
     *     in    ->   out
     *  ( name ) -> ( key )
     *
     * Api: to ( in -> out )
     * Api: from ( out -> in )
     */

    /*
     * inTo
     * 1) The field is Ox field
     * 2) uuid -> ( out -> in )
     * 3) The output structure are Ox field
     */
    public JsonObject inToS(final JsonObject input) {
        return DirectoryKit.process(this.fromData, input, KMapping::from);
    }

    public JsonArray inToS(final JsonArray input) {
        return DirectoryKit.process(input, this::inToS);
    }

    public Future<JsonObject> inTo(final JsonObject input) {
        return Future.succeededFuture(this.inToS(input));
    }

    public Future<JsonArray> inTo(final JsonArray input) {
        return Future.succeededFuture(this.inToS(input));
    }

    /*
     * inFrom
     * 1) The field is Ox field
     * 2) display -> ( in -> out )
     * 3) The output structure are Ox field
     */
    public JsonObject inFromS(final JsonObject input) {
        return DirectoryKit.process(this.fromData, input, KMapping::to);
    }

    public JsonArray inFromS(final JsonArray input) {
        return DirectoryKit.process(input, this::inFromS);
    }

    public Future<JsonObject> inFrom(final JsonObject input) {
        return Future.succeededFuture(this.inFromS(input));
    }

    public Future<JsonArray> inFrom(final JsonArray input) {
        return Future.succeededFuture(this.inFromS(input));
    }

    /*
     * outTo
     * 1) The field is Tp field
     * 2) uuid -> ( out -> in )
     * 3) The output structure are Tp field
     */
    public JsonObject outToS(final JsonObject output) {
        return DirectoryKit.process(this.toData, output, KMapping::from);
    }

    public JsonArray outToS(final JsonArray output) {
        return DirectoryKit.process(output, this::outToS);
    }

    public Future<JsonObject> outTo(final JsonObject input) {
        return Future.succeededFuture(this.outToS(input));
    }

    public Future<JsonArray> outTo(final JsonArray input) {
        return Future.succeededFuture(this.outToS(input));
    }

    /*
     * outFrom
     * 1) The field is Tp field
     * 2) display -> ( in -> out )
     * 3) The output structure are Tp field
     */
    public JsonObject outFromS(final JsonObject output) {
        return DirectoryKit.process(this.toData, output, KMapping::to);
    }

    public JsonArray outFromS(final JsonArray output) {
        return DirectoryKit.process(output, this::outFromS);
    }

    public Future<JsonObject> outFrom(final JsonObject input) {
        return Future.succeededFuture(this.outFromS(input));
    }

    public Future<JsonArray> outFrom(final JsonArray input) {
        return Future.succeededFuture(this.outFromS(input));
    }

    // ----------------------- Operation Method -----------------------
    /*
     * Update single dictionary by `dictName` and `keyField`
     * - dictName: the dictionary name that stored in `dictData`
     *
     * this.init() is required when `dictData` have been changed here
     */
    public void itemUpdate(final String dictName, final JsonObject input) {
        this.itemUpdate(dictName, input, "key");
    }

    public void itemUpdate(final String dictName, final JsonArray input) {
        this.itemUpdate(dictName, input, "key");
    }

    public void itemUpdate(final String dictName, final JsonArray input, final String keyField) {
        this.store.itemUpdate(dictName, input, keyField);
        this.init();
    }

    public void itemUpdate(final String dictName, final JsonObject input, final String keyField) {
        this.store.itemUpdate(dictName, input, keyField);
        this.init();
    }

    /*
     * Check whether there existing the `keyField` = value
     * record in fixed dictName of our DictFabric
     */
    public boolean itemExist(final String dictName, final String value) {
        return this.store.itemExist(dictName, value, "key");
    }

    public boolean itemExist(final String dictName, final String value, final String keyField) {
        return this.store.itemExist(dictName, value, keyField);
    }

    public JsonObject itemFind(final String dictName, final String value) {
        return this.store.itemFind(dictName, value, "key");
    }

    public JsonObject itemFind(final String dictName, final String value, final String keyField) {
        return this.store.itemFind(dictName, value, keyField);
    }

    /*
     * Debug method for output information of current dict only
     */
    public String report() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n\t[ Epsilon ]: ");
        this.epsilonMap.forEach((key, epsilon) -> builder.append("\n\t\t").
            append(key).append(" = ").append(epsilon));
        builder.append("\n\t[ Dict Data ]: ");
        this.store.data().forEach((key, dictData) -> builder.append("\n\t\t").
            append(key).append(" = ").append(dictData.encode()));
        if (Objects.nonNull(this.mapping)) {
            builder.append("\n\t[ Mapping ]: ").append(this.mapping.toString());
        }
        builder.append("\n\t[ From Data ]: ");
        this.fromData.forEach((field, json) -> builder.append("\n\t\t")
            .append(field).append(" = ").append(json.toString()));
        builder.append("\n\t[ To Data ]: ");
        this.toData.forEach((field, json) -> builder.append("\n\t\t")
            .append(field).append(" = ").append(json.toString()));
        return builder.toString();
    }
}
