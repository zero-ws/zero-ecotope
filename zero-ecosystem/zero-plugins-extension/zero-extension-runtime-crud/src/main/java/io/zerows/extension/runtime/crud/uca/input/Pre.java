package io.zerows.extension.runtime.crud.uca.input;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.audit.PreAudit;
import io.zerows.extension.runtime.crud.uca.input.file.PreFile;
import io.zerows.extension.runtime.crud.uca.input.id.PreId;
import io.zerows.extension.runtime.crud.uca.input.qr.PreQr;
import io.zerows.extension.runtime.crud.uca.input.view.PreView;
import io.zerows.plugins.office.excel.ExcelClient;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Pre {

    // ------------------- Utility Pre -------------------
    /*
     * 1) Codex for validation
     * 2) Head values: sigma, id, appKey, language
     * 3) Uri calculation: uri, method
     * 4) Primary Key calculation
     * 5) Excel file calculation
     */
    static Pre codex() {
        return Pooled.CCT_PRE.pick(CodexPre::new, CodexPre.class.getName());
        // Pooled.CC_PRE.pick(CodexPre::new, CodexPre.class.getName());
    }

    static Pre head() {
        return Pooled.CCT_PRE.pick(HeadPre::new, HeadPre.class.getName());
    }

    static Pre uri() {
        return Pooled.CCT_PRE.pick(UriPre::new, UriPre.class.getName());
    }

    static Pre key(final boolean isNew) {
        return PreId.key(isNew);
    }

    static Pre ref() {
        return PreId.ref();
    }

    static Pre excel(final ExcelClient client) {
        return Pooled.CCT_PRE.pick(() -> new ExcelPre(client), ExcelPre.class.getName() + client.hashCode());
    }


    // ------------------- User / Audit Pre -------------------
    /*
     * 1) User information: user, habitus
     * 2) Auditor: createdAt / createdBy / updatedAt / updatedBy
     * 3) Combiner for DictFabric
     * 4) Initial Data
     */
    static Pre user() {
        return Pooled.CCT_PRE.pick(UserPre::new, UserPre.class.getName());
    }

    static Pre audit(final boolean created) {
        return PreAudit.audit(created);
    }

    static Pre audit() {
        return PreAudit.audit();
    }

    // ------------------- Column Related -------------------
    /*
     * 1) number definition for `X_NUMBER`
     * 2) column calculation
     */
    static Pre serial() {
        return Pooled.CCT_PRE.pick(SerialPre::new, SerialPre.class.getName());
    }

    static Pre apeak(final boolean isMy) {
        return PreView.apeak(isMy);
    }

    // ------------------- Import / Export Pre -------------------
    static Pre fileIn(final boolean createOnly) {
        return PreFile.fileIn(createOnly);
    }

    static Pre fileOut() {
        return PreFile.fileOut();
    }

    static Pre fileData() {
        return PreFile.fileData();
    }

    // ------------------- Qr Related -------------------
    /*
     * 1) UniqueKey condition
     * 2) All key condition: sigma = xxx
     * 3) PrimaryKey condition
     * 4) View key
     */
    static Pre qr(final QrType type) {
        return PreQr.qr(type);
    }

    /*
     * Major code execution logical
     * J - JsonObject -> JsonObject
     * A - JsonArray -> JsonArray
     * AJ - JsonArray -> JsonObject
     * JA - JsonObject -> JsonArray
     */
    // JsonObject -> JsonObject
    default Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonArray -> JsonArray
    default Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonArray -> JsonObject
    default Future<JsonObject> inAJAsync(final JsonArray data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonObject -> JsonArray
    default Future<JsonArray> inJAAsync(final JsonObject data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
