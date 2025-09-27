package io.zerows.extension.commerce.rbac.agent.api;

import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.EndPoint;
import io.zerows.core.constant.KName;
import io.zerows.core.web.io.annotations.BodyParam;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.commerce.rbac.eon.Addr;
import io.zerows.extension.commerce.rbac.uca.acl.relation.Junc;
import jakarta.ws.rs.*;

@EndPoint
@Path("/api")
public interface UserAgent {

    /*
     * /api/user/password
     * Request: Update user password here
     */
    @POST
    @Path("user/password")
    @Address(Addr.User.PASSWORD)
    JsonObject password(@BodyParam JsonObject params);

    /*
     * /api/user/profile
     * Request: Update user information
     */
    @POST
    @Path("user/profile")
    @Address(Addr.User.PROFILE)
    JsonObject profile(@BodyParam JsonObject params);

    /*
     * /user/logout
     * 1. Remove token from System
     * 2. Remove pool permission
     */
    @POST
    @Path("user/logout")
    @Address(Addr.Auth.LOGOUT)
    JsonObject logout();

    /**
     * modified by Hongwei at 2019/12/06
     * add get, create, update and delete methods for user domain.
     */
    @GET
    @Path("/user/:key")
    @Address(Addr.User.GET)
    JsonObject getById(@PathParam("key") String key);

    @POST
    @Path("/user")
    @Address(Addr.User.ADD)
    JsonObject create(@BodyParam JsonObject data);

    @PUT
    @Path("/user/:key")
    @Address(Addr.User.UPDATE)
    JsonObject update(@PathParam("key") String key,
                      @BodyParam JsonObject data);

    @DELETE
    @Path("/user/:key")
    @Address(Addr.User.DELETE)
    Boolean delete(@PathParam("key") String key);

    // ---------------- All Usage income api for `user + type` extracting

    /*
     * The user usage in zero extension
     *
     * 1. User Management ( /api/user/search ) RBAC Module
     * 2. 「By Type」Employee Importing
     * 3. 「By Selection」Employee Selecting with usage ( Combine condition )
     */
    @POST
    @Path("/user/search/:identifier")
    @Address(Addr.User.QR_USER_SEARCH)
    JsonObject searchByType(@PathParam(KName.IDENTIFIER) String identifier,
                            @BodyParam JsonObject criteria);

    /**
     * 登录过程读取用户基本信息，步骤
     * <pre><code>
     *     1. 先调用 {@link SUserDao} 直接读取数据表中基本记录
     *     2. 调用 {@link Junc} 三个子接口提取相关配置
     *        2.1. User + Employee 按 Join 读取员工信息或其他相关信息
     *        2.2. 权限信息：
     *             {
     *                 "role": [],
     *                 "group": []
     *             }
     *        2.3. 个人设置，调用 ExSetting 接口提取通道相关信息
     *             {
     *                 "setting": {}
     *             }
     *        2.4. 追加（租户信息）
     *             租户表为一张十分特殊的表，按 code / sigma 维持唯一，简单说一个租户只有一个 code，且只有一个 sigma，sigma 不运行重复
     *             所以也可以直接根据 sigma 提取租户 key 主键，作为 tenantId 的值。
     * </code></pre>
     *
     * @return 返回用户相关信息
     */
    @GET
    @Path("user")
    @Address(Addr.User.INFORMATION)
    JsonObject information();
}
