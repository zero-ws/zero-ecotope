package io.zerows.epoch.corpus;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.metadata.normalize.KRuleTerm;
import io.zerows.epoch.corpus.metadata.commune.Apt;
import io.zerows.enums.typed.ChangeFlag;
import io.zerows.epoch.program.Ut;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-11
 */
class _Rule extends _Plugin {

    /*
     * Rule Match
     * 1. single checking
     * 2. double checking
     * 3. array checking
     */
    public static JsonObject ruleAll(final Collection<KRuleTerm> rules, final JsonObject input) {
        return Unique.ruleAll(rules, input);
    }

    public static ConcurrentMap<Boolean, JsonArray> ruleAll(final Collection<KRuleTerm> rules, final JsonArray input) {
        return Unique.ruleAll(rules, input);
    }

    public static JsonObject ruleAll(final Collection<KRuleTerm> rules, final JsonObject recordO, final JsonObject recordN) {
        return Unique.ruleAll(rules, recordO, recordN);
    }

    public static JsonObject ruleAll(final Collection<KRuleTerm> rules, final JsonArray source, final JsonObject record) {
        return Unique.ruleAll(rules, source, record);
    }

    public static JsonObject ruleAny(final Collection<KRuleTerm> rules, final JsonObject input) {
        return Unique.ruleAny(rules, input);
    }

    public static JsonObject ruleAny(final Collection<KRuleTerm> rules, final JsonObject record0, final JsonObject recordN) {
        return Unique.ruleAny(rules, record0, recordN);
    }

    public static JsonObject ruleAny(final Collection<KRuleTerm> rules, final JsonArray source, final JsonObject record) {
        return Unique.ruleAny(rules, source, record);
    }

    public static ConcurrentMap<Boolean, JsonArray> ruleAny(final Collection<KRuleTerm> rules, final JsonArray input) {
        return Unique.ruleAny(rules, input);
    }

    public static JsonObject ruleTwins(final JsonObject recordO, final JsonObject recordN) {
        return Unique.ruleTwins(recordO, recordN);
    }

    public static JsonObject ruleNil(final JsonObject twins, final ChangeFlag flag) {
        return Unique.ruleNil(twins, flag);
    }

    public static JsonObject ruleNil(final JsonObject recordN, final JsonObject recordO) {
        return Objects.isNull(recordN) ? recordO : recordN;
    }

    public static Apt ruleApt(final JsonArray twins, final boolean isReplaced) {
        return Unique.ruleApt(twins, isReplaced);
    }

    // ------------------------- Compare InJson ------------------------
    /*
     *  1) ruleJOk
     *  2) ruleJReduce
     *  3) ruleJEqual
     *  4) ruleJFind
     */
    public static boolean ruleJOk(final JsonObject record, final Set<String> fields) {
        return Ut.ruleJOk(record, fields);
    }

    public static boolean ruleJOk(final JsonObject record, final JsonArray matrix) {
        return Ut.ruleJOk(record, matrix);
    }

    public static JsonArray ruleJReduce(final JsonArray records, final Set<String> fields) {
        return Ut.ruleJReduce(records, fields);
    }

    public static JsonArray ruleJReduce(final JsonArray records, final JsonArray matrix) {
        return Ut.ruleJReduce(records, matrix);
    }

    public static boolean ruleJEqual(final JsonObject record, final JsonObject latest, final Set<String> fields) {
        return Ut.ruleJEqual(record, latest, fields);
    }

    public static boolean ruleJEqual(final JsonObject record, final JsonObject latest, final JsonArray matrix) {
        return Ut.ruleJEqual(record, latest, matrix);
    }

    public static JsonObject ruleJFind(final JsonArray source, final JsonObject expected, final Set<String> fields) {
        return Ut.ruleJFind(source, expected, fields);
    }

    public static JsonObject ruleJFind(final JsonArray source, final JsonObject expected, final JsonArray matrix) {
        return Ut.ruleJFind(source, expected, matrix);
    }
}
