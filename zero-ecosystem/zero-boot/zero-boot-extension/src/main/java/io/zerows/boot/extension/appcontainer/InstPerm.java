package io.zerows.boot.extension.appcontainer;

import io.r2mo.typed.cc.Cc;

import java.net.URI;
import java.util.Map;

/**
 * 权限资源扫描接口
 * 负责扫描 classpath 下所有 plugins/{MID}/security/ 目录
 */
public interface InstPerm {

    Cc<String, InstPerm> CC_SKELETON = Cc.openThread();

    static InstPerm of() {
        return CC_SKELETON.pick(InstPermLoad::new);
    }

    /**
     * 扫描所有 RBAC_RESOURCE 目录
     * 每个模块的 plugins/{MID}/security/RBAC_RESOURCE/ 目录
     *
     * @return Map&lt;MID, 资源目录URI&gt;
     */
    Map<String, URI> ioResource();

    /**
     * 扫描所有 RBAC_ROLE 目录
     * 每个模块的 plugins/{MID}/security/RBAC_ROLE/ 目录
     *
     * @return Map&lt;MID, 角色目录URI&gt;
     */
    Map<String, URI> ioRole();
}