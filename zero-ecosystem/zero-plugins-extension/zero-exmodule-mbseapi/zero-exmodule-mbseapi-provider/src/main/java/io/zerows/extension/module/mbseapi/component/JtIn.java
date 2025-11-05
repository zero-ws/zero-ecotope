package io.zerows.extension.module.mbseapi.component;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

/*
 * IN_RULE
 * IN_MAPPING
 * IN_PLUG
 * IN_SCRIPT
 */
public interface JtIn {
    Cc<String, JtIn> CC_IN = Cc.openThread();

    static JtIn rule() {
        return CC_IN.pick(JtInRule::new, JtInRule.class.getName());
    }

    static JtIn mapping() {
        return CC_IN.pick(JtInMapping::new, JtInMapping.class.getName());
    }

    static JtIn plug() {
        return CC_IN.pick(JtInPlug::new, JtInPlug.class.getName());
    }

    static JtIn script() {
        return CC_IN.pick(JtInScript::new, JtInScript.class.getName());
    }

    /*
     * IN_RULE
     */
    default Envelop execute(final Envelop envelop, final JtUri uri) {
        /* Default do nothing */
        return envelop;
    }

}

