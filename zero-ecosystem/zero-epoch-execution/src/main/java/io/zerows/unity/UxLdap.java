package io.zerows.unity;

import io.zerows.epoch.common.shared.app.KIntegration;

/*
 * LDAP专用，由于LDAP不会使用第三方库，而是直接使用 javax.naming执行处理，所以不放在 Infusion 架构中，直接提供即可API
 */
public class UxLdap {
    private final KIntegration integration;

    UxLdap(final KIntegration integration) {
        this.integration = integration;
    }
}
