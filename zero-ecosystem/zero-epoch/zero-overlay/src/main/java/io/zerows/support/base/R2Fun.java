package io.zerows.support.base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class R2Fun {

    public static String by() {
        // 从 classpath 根目录加载资源文件
        final InputStream inputStream = R2Fun.class.getClassLoader().getResourceAsStream("system-user");
        if (inputStream == null) {
            throw new RuntimeException("Resource file 'system-user' not found in classpath root.");
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // 读取第一行内容（即 UUID）
            final String systemUser = reader.readLine();
            if (Objects.isNull(systemUser)) {
                return null;
            }
            return systemUser.trim();
        } catch (final Exception e) {
            throw new RuntimeException("[ ZERO ] ( Flyway ) Failed to read resource file 'system-user'.", e);
        }
    }
}
