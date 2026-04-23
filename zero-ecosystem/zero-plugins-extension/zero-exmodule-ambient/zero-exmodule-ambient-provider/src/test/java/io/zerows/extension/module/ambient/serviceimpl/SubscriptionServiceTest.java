package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSubscription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

class SubscriptionServiceTest {

    private final SubscriptionService service = new SubscriptionService();

    @Test
    void shouldRejectPurchaseWhenTenantIdMissing() {
        final JsonObject result = this.service.purchaseApp(null, "sigma", new JsonObject().put("appId", "app-1"))
            .result();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Tenant ID is required", result.getString("error"));
    }

    @Test
    void shouldRejectPurchaseWhenAppIdMissing() {
        final JsonObject result = this.service.purchaseApp("tenant-1", "sigma", new JsonObject())
            .result();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Application ID is required", result.getString("error"));
    }

    @Test
    void shouldReturnEmptyPurchasedWhenTenantIdMissing() {
        final JsonObject result = this.service.searchPurchased(null, new JsonObject()).result();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getInteger("count"));
        Assertions.assertTrue(result.getJsonArray("list").isEmpty());
    }

    @Test
    void shouldFilterPurchasedByKeywordAndPaginate() throws Exception {
        final Method filterMethod = SubscriptionService.class.getDeclaredMethod(
            "filterPurchased", List.class, JsonObject.class
        );
        filterMethod.setAccessible(true);

        final Method paginateMethod = SubscriptionService.class.getDeclaredMethod(
            "paginatePurchased", List.class, JsonObject.class
        );
        paginateMethod.setAccessible(true);

        final List<JsonObject> items = List.of(
            new JsonObject().put("title", "商城应用 A").put("name", "Store A").put("code", "APP-A"),
            new JsonObject().put("title", "流程应用 B").put("name", "Flow B").put("code", "APP-B"),
            new JsonObject().put("title", "商城应用 C").put("name", "Store C").put("code", "APP-C")
        );
        final JsonObject query = new JsonObject()
            .put("criteria", new JsonObject().put("keyword", "商城"))
            .put("pager", new JsonObject().put("page", 1).put("size", 1));

        @SuppressWarnings("unchecked")
        final List<JsonObject> filtered = (List<JsonObject>) filterMethod.invoke(this.service, items, query);
        final JsonArray paged = (JsonArray) paginateMethod.invoke(this.service, filtered, query);

        Assertions.assertEquals(2, filtered.size());
        Assertions.assertEquals(1, paged.size());
        Assertions.assertEquals("商城应用 A", paged.getJsonObject(0).getString("title"));
    }

    @Test
    void shouldBuildPurchasedItemWithSubscriptionVersionAndStatus() throws Exception {
        final Method method = SubscriptionService.class.getDeclaredMethod(
            "toPurchasedItem", XSubscription.class, XApp.class
        );
        method.setAccessible(true);

        final XSubscription subscription = new XSubscription();
        subscription.setId("sub-1");
        subscription.setAppId("app-1");
        subscription.setCreatedAt(LocalDateTime.of(2026, 4, 17, 10, 0));
        subscription.setEndAt(LocalDateTime.now().plusDays(3));
        subscription.setStatus("ACTIVE");
        subscription.setActive(Boolean.TRUE);
        subscription.setVersion("2.0.0");

        final XApp app = new XApp();
        app.setId("app-1");
        app.setName("store-app");
        app.setTitle("商城应用");
        app.setLogo("logo.svg");
        app.setCode("STORE-APP");
        app.setVersion("1.0.0");

        final JsonObject item = (JsonObject) method.invoke(this.service, subscription, app);

        Assertions.assertEquals("sub-1", item.getString("id"));
        Assertions.assertEquals("STORE-APP", item.getString("code"));
        Assertions.assertEquals("2.0.0", item.getString("version"));
        Assertions.assertEquals("EXPIRING_SOON", item.getString("status"));
        Assertions.assertTrue(item.getBoolean("active"));
    }
}
