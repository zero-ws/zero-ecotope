package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.bootstrap.AckFlow;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

/**
 * The handler chain contains 4 rules in sequence, it's for complex routing design
 * ------
 * 1. 「Optional」This rule will action IN_RULE for validation, there should be some
 * components that could be defined, now it support VALIDATOR / CONVERTER instead. These
 * components will provide response message to client and it will process in priority such
 * as 400 bad request / 400 parameter required exception here.
 * ------
 * 2. 「Optional」The second step to action IN_MAPPING, it's for field mapping and the
 * first time convert the field data in request. The output will be input data that will
 * be used in next step.
 * ------
 * 3. 「Optional」It's defined by java code ( IN_PLUG ) and reflect to of to plug-in
 * ------
 * 4. 「Optional」The last step is executing JS Engine ( JavaScript, IN_SCRIPT ), the javascript
 * could call following service component, also it's dynamic to define different code-logical and
 * income requirement. Here it's easy to expand. It's more smart.
 * ------
 * The final execution sequence is as following：
 * IN_RULE -> IN_MAPPING -> IN_PLUG -> IN_SCRIPT
 */
public class JtAimIn implements JtAim {

    @Override
    public Handler<RoutingContext> attack(final JtUri uri) {
        /*
         * 「The lifecycle of starting」
         * 1. IN_RULE Specification
         * 2. IN_MAPPING Specification
         * 3. IN_PLUG Specification
         * 4. IN_SCRIPT Specification
         */
        return context -> {
            Envelop request = AckFlow.previous(context);
            // IN_RULE
            request = JtIn.rule().execute(request, uri);
            if (!request.valid()) {
                AckFlow.reply(context, request);
            }
            // IN_MAPPING
            request = JtIn.mapping().execute(request, uri);
            if (!request.valid()) {
                AckFlow.reply(context, request);
            }
            // IN_PLUG
            request = JtIn.plug().execute(request, uri);
            if (!request.valid()) {
                AckFlow.reply(context, request);
            }
            // IN_SCRIPT
            request = JtIn.script().execute(request, uri);
            /*
             * Here should not resume directly because the data must be bind to Envelop
             * All the data should be stored into Envelop
             */
            AckFlow.normalize(context, request);
        };
    }
}
