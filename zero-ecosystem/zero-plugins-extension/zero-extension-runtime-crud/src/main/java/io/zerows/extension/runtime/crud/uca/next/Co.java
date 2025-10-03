package io.zerows.extension.runtime.crud.uca.next;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.zerows.exception.web._60050Exception501NotSupport;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;

import java.util.List;

/**
 * The interface for module selection
 * Phase 1: Combine input request into IxOpt and pass to IxPanel
 *
 * 1) Combine input ( Envelop, Body, Module ) three formatFail
 * 2) Calculate the result to IxOpt
 *
 * > This component will be called by IxOpt internal
 *
 *
 * 「Sequence」
 * Phase 2: For sequence only
 *
 * 1) Execute `active` function
 * 2) Pass the result of `active` into `standBy` ( Tran Component )
 * 3) Execute `standBy` function
 *
 * Phase 3: Response building
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public interface Co<I, A, S, O> {

    static Co nextQ(final IxMod in, final boolean isArray) {
        if (isArray) {
            return Pooled.CCT_CO.pick(() -> new NtAQr(in), NtAQr.class.getName() + in.cached());
        } else {
            return Pooled.CCT_CO.pick(() -> new NtJQr(in), NtJQr.class.getName() + in.cached());
        }
    }

    static Co nextJ(final IxMod in, final boolean isArray) {
        if (isArray) {
            return Pooled.CCT_CO.pick(() -> new NtAData(in), NtAData.class.getName() + in.cached());
        } else {
            return Pooled.CCT_CO.pick(() -> new NtJData(in), NtJData.class.getName() + in.cached());
        }
    }

    static Co endV(final boolean isMy) {
        if (isMy) {
            return Pooled.CCT_CO.pick(OkAActive::new, "ApeakMy:" + OkAActive.class.getName());
        } else {
            return Pooled.CCT_CO.pick(OkAApeak::new, OkAApeak.class.getName());
        }
    }

    static Co endE(final List<String> columns) {
        return Pooled.CCT_CO.pick(() -> new OkAExport(columns),
            OkAExport.class.getName() + columns.hashCode());
    }

    /*
     * 「Response」
     * InJson + InJson -----> InJson
     *
     * active - The first executor result
     * standBy - The second executor result
     * response - The API final result
     */
    default Future<O> ok(final A active, final S standBy) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    /*
     * 「Middle」
     * InJson + InJson -----> InJson
     * input - The input data of the first executor
     * active - The first executor result
     * standBy - The standBy result
     */
    default Future<S> next(final I input, final A active) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Co<I, A, S, O> bind(final IxRequest request) {
        return this;
    }
}