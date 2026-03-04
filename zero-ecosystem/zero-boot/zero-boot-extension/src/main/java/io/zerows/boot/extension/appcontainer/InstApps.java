package io.zerows.boot.extension.appcontainer;

import io.r2mo.typed.cc.Cc;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface InstApps {

    Cc<String, InstApps> CC_SKELETON = Cc.openThread();

    static InstApps of() {
        return CC_SKELETON.pick(InstAppsLoad::new);
    }

    List<URI> ioApp();

    List<URI> ioRunning();

    /**
     * 读取 apps/instance.yml 中的 UUID 映射
     * 格式：running.yml 根节点，UUID=name
     *
     * @return Map<name, UUID>
     */
    Map<String, String> ioInstance();

    /**
     * 读取 apps/instance.yml 中的 init 配置
     * 格式：init 根节点，包含 menu、app 等配置
     *
     * @return JsonObject，包含 init 配置
     */
    io.vertx.core.json.JsonObject ioInit();
}
