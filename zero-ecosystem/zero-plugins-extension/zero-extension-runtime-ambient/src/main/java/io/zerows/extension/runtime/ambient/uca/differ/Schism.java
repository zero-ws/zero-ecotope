package io.zerows.extension.runtime.ambient.uca.differ;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivity;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HAtom;

import java.util.function.Supplier;

/**
 * Split the data source into different part here
 * 1. New interface for output processing: Returned to JSix
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Schism {

    Cc<String, Schism> CC_SCHISM = Cc.openThread();

    static Schism diffJ(final HAtom atom) {
        final HArk ark = atom.ark();
        final String unique = ark.sigma() + VString.SLASH + atom.identifier() + VString.SLASH + SchismJ.class.getName();
        return CC_SCHISM.pick(SchismJ::new, unique).bind(atom);
    }

    /*
     * Bind the definition of argument, the data structure is as following:
     *
     */
    Schism bind(HAtom atom);

    // ============================= Generate XActivity Records ========================
    /*
     * Diff on record to generate activity record
     * Generate twins data structure
     * {
     *     "__OLD__": {},
     *     "__NEW__": {}
     * }
     */
    Future<JsonObject> diffAsync(JsonObject recordO, JsonObject recordN, Supplier<Future<XActivity>> activityFn);
}
