package io.zerows.extension.module.rbac.component.acl.relation;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ScTie;
import io.zerows.extension.skeleton.spi.ScTwine;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class Junc {

    private static final Cc<String, ScTwine> CC_TWINE = Cc.openThread();

    private static final Cc<String, ScTie> CC_TIE = Cc.openThread();

    /*
     * ExUser 内置调用，处理 modelId / modelKey 专用
     * -- 旧版本使用 modelId / modelKey 更新 S_USER 专用
     **/
    public static ScTwine<String> refModel() {
        return (ScTwine<String>) CC_TWINE.pick(TwineModel::new, TwineModel.class.getName());
    }

    /*
     * 原 UserExtension，根据配置文件处理专用操作用户和其他账号的链接
     * -- 新版本使用 S_USER 和配置中对应类型的账号相关联专用
     * */
    public static ScTwine<SUser> refExtension() {
        return (ScTwine<SUser>) CC_TWINE.pick(TwineExtension::new, TwineExtension.class.getName());
    }

    public static ScTwine<String> refRights() {
        return (ScTwine<String>) CC_TWINE.pick(TwineRights::new, TwineRights.class.getName());
    }

    public static ScTie<String, JsonArray> role() {
        return (ScTie<String, JsonArray>) CC_TIE.pick(TieRole::new, TieRole.class.getName());
    }

    public static ScTie<String, JsonArray> group() {
        return (ScTie<String, JsonArray>) CC_TIE.pick(TieGroup::new, TieGroup.class.getName());
    }

    public static ScTwine<SUser> refSetting() {
        return (ScTwine<SUser>) CC_TWINE.pick(TwineSetting::new, TwineSetting.class.getName());
    }
}
