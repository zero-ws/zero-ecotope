package io.zerows.support.base;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HAtom;
import io.zerows.specification.modeling.HRecord;

/**
 * 全称为 High Order Modeler Service，建模专用工具类，替换原来的
 * HUt 的 Modeler 部分的核心调用，该工具从 HUt 继承，属于上层工具原型链
 * HUt 可以直接调用 _App / HNc 形成统一归口，依旧走 HUt 工具链
 * 三个工具层主管不同的工具，而且形成完整原型链
 * 注：_App 的所有注释部分全部挪到 HUt 的统一归口中
 *
 * @author lang : 2023-05-08
 */
class _App {
    protected _App() {
    }

    public static JsonArray toJArray(final HRecord[] records) {
        return MModeler.toJArray(records);
    }

    // 名空间计算
    public static String nsApp(final String appName) {
        return MAtom.namespace(appName, null);
    }

    public static String nsAtom(final String appName, final String identifier) {
        return MAtom.namespace(appName, identifier);
    }

    // 键计算
    public static String keyAtom(final HAtom atom, final JsonObject options) {
        return MAtom.keyAtom(atom, options);
    }

    public static String keyApp(final String name, final String owner) {
        final String ns = MAtom.namespace(name, null);
        return MContext.keyApp(name, ns, owner);
    }

    public static String keyApp(final HArk ark) {
        return MContext.keyApp(ark);
    }

    public static String keyOwner(final String id) {
        return MContext.keyOwner(id);
    }

    public static JsonObject qrApp(final HArk ark, final EmApp.Mode mode) {
        return MContext.qrApp(ark, mode);
    }

    public static JsonObject qrService(final HArk ark, final EmApp.Mode mode) {
        return MContext.qrService(ark, mode);
    }

    // 查询条件

}
