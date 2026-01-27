package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.InputStream;
import java.util.List;

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

    JsonObject inYamlJ(String filename);

    JsonArray inYamlA(String filename);

    JsonObject inJObject(String filename);

    JsonArray inJArray(String filename);

    InputStream inStream(String filename);

    List<String> ioDirectories(String directory);

    default List<String> ioFiles(final String directory) {
        return this.ioFiles(directory, null);
    }

    List<String> ioFiles(String directory, String suffix);

    boolean ioExist(String path);
}
