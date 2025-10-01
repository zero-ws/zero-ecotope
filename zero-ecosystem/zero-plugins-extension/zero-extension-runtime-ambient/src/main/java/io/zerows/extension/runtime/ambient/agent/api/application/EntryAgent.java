package io.zerows.extension.runtime.ambient.agent.api.application;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.extension.runtime.ambient.eon.Addr;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * 应用入口接口，场景
 * <pre><code>
 *     1. 入口读取应用基础数据
 *        - 此时读取不考虑 appSecret / appKey 两个字段值
 *        - （公开）接口
 *     2. 登录完成之后根据环境变量读取登录应用，并且设置当前登录的 app 为 默认
 *        - isDefault = true
 *     3. 读取当前环境中所有的菜单（根据 App 过滤）
 *     4. 读取应用基本信息：右上应用信息基本菜单
 * </code></pre>
 *
 * @author lang : 2024-07-26
 */
@EndPoint
public interface EntryAgent {
    /**
     * 登录主界面根据应用名读取应用基础数据
     * <pre><code>
     *     1. 启动的前端 Z_APP 的环境变量标识了登录应用的默认
     *        isDefault = true
     *     2. 根据 X_TENANT 读取应用列表
     *        status = RUNNING
     *        状态不对的直接过滤掉不读取
     * </code></pre>
     *
     * @param name 应用名称
     *
     * @return 应用基础数据
     */
    @Path("/app/name/{name}")
    @GET
    @Address(Addr.App.BY_NAME)
    JsonObject appByName(@PathParam("name") String name);

    /**
     * 读取应用之下所有的菜单
     *
     * @param appId 应用ID
     *
     * @return 菜单数据
     */
    @Path("/api/menus")
    @GET
    @Address(Addr.Menu.BY_APP_ID)
    JsonArray menuByApp(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);

    /**
     * 登录之后读取应用信息，此处读取内容包括
     * <pre><code>
     *     1. 加上 appSecret / appKey 两个字段
     *     2. 加上 B_BLOCK 配置字段，如 mStore = ???
     *        mSetting = SSM 配置结构
     *     3. 加上附加信息，附加信息可以直接通过 appKey 读取
     * </code></pre>
     *
     * @param appId 应用ID
     *
     * @return 应用基础数据
     */
    @Path("/api/app")
    @GET
    @Address(Addr.App.BY_ID)
    JsonObject appById(@HeaderParam(KWeb.HEADER.X_APP_ID) String appId);
}
