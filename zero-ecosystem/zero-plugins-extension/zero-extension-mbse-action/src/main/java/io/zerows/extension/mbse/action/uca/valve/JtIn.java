package io.zerows.extension.mbse.action.uca.valve;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;

/*
 * IN_RULE
 * IN_MAPPING
 * IN_PLUG
 * IN_SCRIPT
 */
public interface JtIn {
    Cc<String, JtIn> CC_IN = Cc.openThread();

    static JtIn rule() {
        return CC_IN.pick(RuleValve::new, RuleValve.class.getName());
    }

    static JtIn mapping() {
        return CC_IN.pick(MappingValve::new, MappingValve.class.getName());
    }

    static JtIn plug() {
        return CC_IN.pick(PlugValve::new, PlugValve.class.getName());
    }

    static JtIn script() {
        return CC_IN.pick(ScriptValve::new, ScriptValve.class.getName());
    }

    /*
     * IN_RULE
     */
    default Envelop execute(final Envelop envelop, final JtUri uri) {
        /* Default do nothing */
        return envelop;
    }

}

