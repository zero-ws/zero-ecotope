package io.zerows.extension.commerce.rbac.util;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Log;
import io.zerows.component.log.LogModule;
import io.zerows.epoch.sdk.security.Acl;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.OUser;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPacket;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPath;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Sc {

    public static Future<JsonObject> cachePath(final SPath path, final Function<SPath, Future<JsonObject>> executor) {
        return ScCache.admitPath(path, executor, "PATH");
    }

    public static Future<List<SPacket>> cachePocket(final SPath path, final Function<SPath, Future<List<SPacket>>> executor) {
        return ScCache.admitPath(path, executor, "POCKET");
    }

    public static Future<JsonObject> cacheView(final RoutingContext context, final String habitus) {
        return ScCache.view(context, habitus);
    }

    public static String valuePassword() {
        return ScGenerated.valuePassword();
    }

    public static Buffer valueImage(final String imageCode, final int width, final int height) {
        return ScImage.valueImage(imageCode, width, height);
    }

    public static String valueProfile(final SResource resource) {
        return ScGenerated.valueProfile(resource);
    }

    public static Future<OUser> valueAuth(final SUser user, final JsonObject inputJ) {
        return ScGenerated.valueAuth(user, inputJ);
    }

    public static Future<List<OUser>> valueAuth(final List<SUser> users) {
        return ScGenerated.valueAuth(users);
    }

    public static Future<List<SUser>> valueAuth(final JsonArray userA, final String sigma) {
        return ScGenerated.valueAuth(userA, sigma);
    }

    /*
     * Lock Part
     * - lockOn, when handler, the counter increased
     * - lockOff, when success, the counter cleared
     * - lockVerify, when before login, verify the specification first
     */
    public static Future<JsonObject> lockVerify(final String username, final Supplier<Future<JsonObject>> executor) {
        return ScLock.lockVerify(username, executor);
    }

    public static Future<Integer> lockOn(final String username) {
        return ScLock.lockOn(username);
    }

    public static Future<Integer> lockOff(final String username) {
        return ScLock.lockOff(username);
    }

    public static Future<Buffer> imageGenerated(final String code) {
        return ScImage.imageGenerated(code);
    }

    /*
     * Acl method
     */
    public static JsonArray aclOn(final JsonArray original, final Acl acl) {
        return ScAcl.aclOn(original, acl);
    }

    public static void aclRecord(final JsonObject record, final Acl acl) {
        ScAcl.aclRecord(record, acl);
    }

    public interface LOG {
        String MODULE = "Ακριβώς";

        LogModule Auth = Log.modulat(MODULE).extension("Auth");
        LogModule Web = Log.modulat(MODULE).extension("Web");
        LogModule View = Log.modulat(MODULE).extension("View");
        LogModule Visit = Log.modulat(MODULE).extension("Visit");
        LogModule Resource = Log.modulat(MODULE).extension("Resource");
        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Credit = Log.modulat(MODULE).extension("Credit");
        LogModule Audit = Log.modulat(MODULE).extension("Audit");
    }
}
