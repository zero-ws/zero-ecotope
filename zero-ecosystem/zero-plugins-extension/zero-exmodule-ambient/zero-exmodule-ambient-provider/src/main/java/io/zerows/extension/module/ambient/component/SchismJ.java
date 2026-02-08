package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.boot.At;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivity;
import io.zerows.extension.module.ambient.domain.tables.pojos.XActivityChange;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * # Activity/Changes Operation
 * <p>
 * The main record of info history ( Activity Record )
 * <p>
 * - key                    ( System Generated )
 * - serial                 ( System Generated )            - 「Workflow」,「Atom」
 * - type                   ( The Record type )
 * - description            ( Description )                 - 「Workflow」,「Atom」
 * <p>
 * - modelId                ( Modal identifier )
 * - modelKey               ( Model Related key )
 * - modelCategory          ( Model Category )
 * <p>
 * - taskName               ( Workflow Provided )           - 「Workflow」,「Atom」
 * - taskSerial             ( Workflow Provided )           - 「Workflow」,「Atom」
 * - recordOld              ( Old InJson )
 * - recordNew              ( New InJson )
 * <p>
 * - sigma                  ( InJson Provided )
 * - active                 ( InJson Provided )
 * - language               ( InJson Provided )
 * - metadata               ( Empty )
 * - createdAt              ( Outer Now )                   - 「Workflow」,「Atom」
 * - createdBy              ( Outer Current )               - 「Workflow」,「Atom」
 * - updatedAt              ( Outer Now )
 * - updatedBy              ( Outer Current )
 * <p>
 * The sub records of history ( ActivityChange Record )
 * <p>
 * - key                    ( System Generated )
 * - activityId             ( System Generated )
 * <p>
 * - type                   ( System Generated, ADD, UPDATE, DELETE )
 * - status                 ( CONFIRMED / PENDING / SYSTEM )
 * <p>
 * - fieldName              ( Came from Atom )
 * - fieldAlias             ( Came from Atom )
 * - fieldType              ( Came from Atom )
 * - valueOld               ( Came from Data )
 * - valueNew               ( Came from Data )
 * <p>
 * - sigma                  ( InJson Provided )
 * - language               ( InJson Provided )
 * - active                 ( InJson Provided )
 * - metadata               ( Empty )
 * <p>
 * - createdBy              ( Outer Current )
 * - createdAt              ( Outer Now )
 * - updatedBy              ( Outer Current )
 * - updatedAt              ( Outer Now )
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class SchismJ extends SchismBase {
    @Override
    public Future<JsonObject> diffAsync(
        final JsonObject recordO, final JsonObject recordN, final Supplier<Future<XActivity>> activityFn) {
        Objects.requireNonNull(activityFn);
        /*
         * The activityFn will create new XActivity record for current comparing
         * 1) The basic condition is Ok
         * 2) The required field will be filled in current method
         */
        return activityFn.get().compose(activity -> {
            /*
             *  Here the two fields are ready:
             *  - key
             *  - serial
             */
            activity.setRecordOld(recordO);
            activity.setRecordNew(recordN);

            final List<XActivityChange> changes = At.diffChange(recordO, recordN, this.atom);

            At.diffChange(changes, activity);
            return this.createActivity(activity, changes);
        });
    }
}
