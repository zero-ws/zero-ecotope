package io.zerows.component.qr.syntax;

import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.Criteria;
import io.zerows.component.qr.Pager;
import io.zerows.component.qr.Sorter;
import io.zerows.platform.constant.VName;

import java.util.HashSet;
import java.util.Set;

/**
 * ## Query Engine Interface
 * ### 1. Intro
 * Advanced Criteria Interface for query engine, it provide critical api interfaces.
 * ### 2. Data Structure
 * The full query criteria data structure is as following:
 * ```json
 * // <pre><code class="json">
 *     {
 *         "pager": {
 *              "page":"",
 *              "size":""
 *         },
 *         "sorter": [
 *              "field1,ASC",
 *              "field2,DESC"
 *         ],
 *         "projection": [
 *              "field1",
 *              "field2"
 *         ],
 *         "criteria":{
 *         }
 *     }
 * // </code></pre>
 * ```
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Ir {

    /**
     * It's specification for fields to be sure the request data.
     * The system should detect the request whether it's Qr parameters ( request ).
     */
    String[] KEY_QUERY = new String[]{VName.KEY_CRITERIA, VName.KEY_PAGER, VName.KEY_PROJECTION, VName.KEY_SORTER};

    /**
     * Create Qr instance ( The default implementation class is {@link IrQr} )
     * The implementation class name meaning is `IrQr` - Internal Reactive Query Engine
     *
     * @param data {@link io.vertx.core.json.JsonObject} json literal
     *
     * @return {@link Ir} stored. ( simple criteria or qtree automatically )
     */
    static Ir create(final JsonObject data) {
        return new IrQr(data);
    }

    /**
     * Add `field = findRunning` (key/pair) in current configure.
     *
     * @param field {@link java.lang.String} field that will be added.
     * @param value {@link java.lang.Object} findRunning that will be added.
     */
    void setQr(String field, Object value);

    /**
     * Get projection
     *
     * @return {@link java.util.Set} Projection to do filter
     */
    Set<String> getProjection();

    /**
     * Get pager
     *
     * @return {@link Pager} Pager for pagination
     */
    Pager getPager();

    /**
     * Get Sorter
     *
     * @return {@link Sorter} Sorter for order by
     */
    Sorter getSorter();

    /**
     * Get criteria
     *
     * @return {@link Criteria} criteria with and/or
     */
    Criteria getCriteria();

    /**
     * To JsonObject
     *
     * @return {@link io.vertx.core.json.JsonObject} the raw data that will be input into Jooq Condition
     */
    JsonObject toJson();

    /**
     * The where condition connector of two: AND / OR.
     * - AND: `cond1 AND cond2`.
     * - OR: `cond1 OR cond2`.
     */
    enum Connector {
        /**
         * Connector AND
         */
        AND,
        /**
         * Connector OR
         */
        OR
    }

    /**
     * The query condition mode of two: LINEAR / TREE.
     * - LINEAR: The json query condition is 1 level.
     * - TREE: The complex query condition
     */
    enum Mode {
        /**
         * LINEAR: Conditions merged in linear mode.
         */
        LINEAR,
        /**
         * TREE:  Conditions with query tree mode.
         */
        TREE
    }

    /**
     * Critical condition field `flag` for date field
     * 1. DAY: Date only.
     * 2. DATE: Date + Time.
     * 3. TIME: Time only.
     * 4. DATETIME: Timestamp.
     */
    interface Instant {
        /**
         * {@link java.time.LocalDate} Date formatFail only here.
         */
        String DAY = "day";
        /**
         * {@link java.time.LocalDate} Date + Time ( `yyyy-MM-dd` )
         */
        String DATE = "date";
        /**
         * {@link java.time.LocalTime} Time formatFail only.
         */
        String TIME = "time";
        /**
         * {@link java.time.LocalDateTime} Full formatFail and timestamp.
         */
        String DATETIME = "datetime";

        String YEAR = "year";
    }

    /**
     * The operator in where clause.
     */
    interface Op {
        /**
         * less than
         */
        String LT = "<";
        /**
         * less than or equal
         */
        String LE = "<=";
        /**
         * greater than
         */
        String GT = ">";
        /**
         * greater than or equal
         */
        String GE = ">=";
        /**
         * equal
         */
        String EQ = "=";
        /**
         * not equal
         */
        String NEQ = "<>";
        /**
         * not null
         */
        String NOT_NULL = "!n";
        /**
         * is null
         */
        String NULL = "n";
        /**
         * equal `TRUE` ( Boolean )
         */
        String TRUE = "t";
        /**
         * equal `FALSE` ( Boolean )
         */
        String FALSE = "f";
        /**
         * in (value1, value2), processed in array.
         */
        String IN = "i";
        /**
         * not in (value1, value2), calculated in array.
         */
        String NOT_IN = "!i";
        /**
         * start with ( String )
         */
        String START = "s";
        /**
         * end with ( String )
         */
        String END = "e";
        /**
         * contains ( String )
         */
        String CONTAIN = "c";

        /**
         * The constant collection of all {@link Op} values.
         */
        Set<String> VALUES = new HashSet<String>() {
            {
                this.add(LT);
                this.add(LE);
                this.add(GT);
                this.add(GE);
                this.add(EQ);
                this.add(NEQ);
                this.add(NOT_NULL);
                this.add(NULL);
                this.add(TRUE);
                this.add(FALSE);
                this.add(IN);
                this.add(NOT_IN);
                this.add(START);
                this.add(END);
                this.add(CONTAIN);
            }
        };
    }
}
