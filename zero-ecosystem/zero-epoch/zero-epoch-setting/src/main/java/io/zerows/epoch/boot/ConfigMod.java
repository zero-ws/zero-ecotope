package io.zerows.epoch.boot;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.spi.HPI;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * 加载文件路径中的模块配置
 * <pre>
 *     1. 加载 yml 配置文件
 *     2. 加载 json 配置文件
 *     3. 其他文件返回 {@link InputStream} 输入流
 * </pre>
 *
 * @author lang : 2025-12-15
 */
public interface ConfigMod {

    Cc<String, ConfigMod> CC_SKELETON = Cc.openThread();

    static ConfigMod of() {
        return of(null);
    }

    static ConfigMod of(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return CC_SKELETON.pick(() -> HPI.findOneOf(ConfigMod.class), "DEFAULT");
        }
        return CC_SKELETON.pick(() -> SourceReflect.instance(clazz), clazz.getName());
    }

    JsonObject inYamlJ(String filename);

    JsonArray inYamlA(String filename);

    JsonObject inJObject(String filename);

    InputStream inStream(String filename);

    List<String> ioDirectories(String directory);

    default List<String> ioFiles(final String directory) {
        return this.ioFiles(directory, null);
    }

    List<String> ioFiles(String directory, String suffix);

    boolean ioExist(String path);
}
