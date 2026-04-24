package io.zerows.extension.module.ambient.serviceimpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppServiceTest {

    @Test
    void shouldAcceptPublishableStatus() {
        Assertions.assertTrue(AppService.isPublishableStatus("DEPLOYED"));
        Assertions.assertTrue(AppService.isPublishableStatus("RUNNING"));
        Assertions.assertTrue(AppService.isPublishableStatus("APP.DEPLOYED"));
        Assertions.assertTrue(AppService.isPublishableStatus("APP.RUNNING"));
        Assertions.assertTrue(AppService.isPublishableStatus(" app.running "));
    }

    @Test
    void shouldRejectNonPublishableStatus() {
        Assertions.assertFalse(AppService.isPublishableStatus(null));
        Assertions.assertFalse(AppService.isPublishableStatus(""));
        Assertions.assertFalse(AppService.isPublishableStatus("DRAFT"));
        Assertions.assertFalse(AppService.isPublishableStatus("APP.STOPPED"));
    }
}
