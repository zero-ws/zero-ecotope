package io.zerows.extension.module.ambient.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RbacResourceSubscriptionMyTest {

    private static final String BASE =
        "plugins/zero-exmodule-ambient/security/RBAC_RESOURCE/云端管理/订阅管理/订阅查看/";

    @Test
    void shouldContainMySubscriptionActionResourceDefinition() {
        assertResourceExists("PERM.yml");
        assertResourceExists("我的订阅@GET@_api_subscription_my.yml");
    }

    private static void assertResourceExists(final String filename) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Assertions.assertNotNull(
            classLoader.getResource(BASE + filename),
            () -> "missing RBAC resource: " + BASE + filename
        );
    }
}
