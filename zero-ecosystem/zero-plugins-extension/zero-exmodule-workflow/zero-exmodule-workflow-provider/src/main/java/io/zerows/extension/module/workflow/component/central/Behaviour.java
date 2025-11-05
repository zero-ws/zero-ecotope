package io.zerows.extension.module.workflow.component.central;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.metadata.MetaInstance;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Behaviour {
    /*
     * Component ConfigRunner Binding
     */
    Behaviour bind(JsonObject config);

    /*
     * 1. Ticket, Todo, Linkage ConfigRunner
     * 2. Record / Entity ConfigRunner
     */
    Behaviour bind(MetaInstance metadata);
}
