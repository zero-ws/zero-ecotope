package io.zerows.extension.module.rbac.component.acl.relation;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ScLink;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class LinkManager {

    private static final Cc<String, ScLink.Extension> CC_TWINE = Cc.openThread();

    private static final Cc<String, ScLink> CC_TIE = Cc.openThread();

    /*
     * ExUser 内置调用，处理 modelId / modelKey 专用
     * -- 旧版本使用 modelId / modelKey 更新 S_USER 专用
     **/
    public static ScLink.Extension<String> refModel() {
        return (ScLink.Extension<String>) CC_TWINE.pick(LinkExModel::new, LinkExModel.class.getName());
    }

    /*
     * 原 UserExtension，根据配置文件处理专用操作用户和其他账号的链接
     * -- 新版本使用 S_USER 和配置中对应类型的账号相关联专用
     * */
    public static ScLink.Extension<SUser> refExtension() {
        return (ScLink.Extension<SUser>) CC_TWINE.pick(LinkExAuthority::new, LinkExAuthority.class.getName());
    }

    public static ScLink.Extension<String> refPerms() {
        return (ScLink.Extension<String>) CC_TWINE.pick(LinkExPerm::new, LinkExPerm.class.getName());
    }

    public static ScLink<String, JsonArray> role() {
        return (ScLink<String, JsonArray>) CC_TIE.pick(LinkRole::new, LinkRole.class.getName());
    }

    public static ScLink<String, JsonArray> group() {
        return (ScLink<String, JsonArray>) CC_TIE.pick(LinkGroup::new, LinkGroup.class.getName());
    }

    public static ScLink.Extension<SUser> refSetting() {
        return (ScLink.Extension<SUser>) CC_TWINE.pick(LinkExSetting::new, LinkExSetting.class.getName());
    }
}
