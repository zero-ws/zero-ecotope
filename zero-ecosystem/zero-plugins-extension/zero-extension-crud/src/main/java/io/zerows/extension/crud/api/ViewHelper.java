package io.zerows.extension.crud.api;

import io.zerows.extension.crud.uca.desk.IxPanel;
import io.zerows.extension.crud.uca.desk.IxRequest;
import io.zerows.extension.crud.uca.input.Pre;
import io.zerows.extension.crud.uca.next.Co;
import io.zerows.extension.crud.uca.op.Agonic;

/**
 * @author lang : 2025-11-05
 */
class ViewHelper {
    /*
     * Shared Method mask as static method for two usage
     */
    @SuppressWarnings("all")
    static IxPanel fetchFull(final IxRequest request) {
        return IxPanel.on(request)
            .input(
                Pre.apeak(false)::inJAsync,             /* Apeak */
                Pre.head()::inJAsync                    /* Header */
            )
            /*
             * {
             *     "identifier": "Model identifier",
             *     "view": "The view name, if not put DEFAULT",
             *     "dynamic": "true if use dynamic",
             *     "sigma": "The application uniform"
             * }
             */
            .parallel(/* Active */Agonic.view(false)::runJAAsync)
            .output(/* Columns connected */Co.endV(false)::ok);
    }
}
