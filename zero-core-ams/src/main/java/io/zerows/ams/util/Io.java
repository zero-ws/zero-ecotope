package io.zerows.ams.util;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VString;
import io.zerows.core.exception.boot._11002Exception500EmptyIo;
import io.zerows.core.exception.boot._11004Exception415JsonFormat;
import io.zerows.core.uca.log.LogUtil;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * The library for IO resource reading.
 */
final class Io {

    /**
     * 「DEAD-LOCK」LoggerFactory.getLogger
     * Do not use `Annal` logger because of deadlock.
     */
    private static final LogUtil LOG = LogUtil.from(Io.class);

    private Io() {
    }

    static JsonArray ioJArray(final InputStream in) {
        final JsonArray content;
        try {
            content = new JsonArray(ioString(in, null));
        } catch (final Throwable ex) {
            throw new _11004Exception415JsonFormat("Stream/JArray");
        }
        return content;
    }

    static JsonArray ioJArray(final URL url) {
        if (Objects.isNull(url)) {
            return new JsonArray();
        }
        return Fn.jvmAs(
            url::openStream, Io::ioJArray,
            () -> new _11002Exception500EmptyIo("URL/JArray: " + url.getPath())
        );
    }

    static JsonArray ioJArray(final String filename) {
        final JsonArray content;
        try {
            content = new JsonArray(ioString(filename, null));
        } catch (final Throwable ex) {
            throw new _11004Exception415JsonFormat(filename);
        }
        return content;
    }

    static JsonObject ioJObject(final String filename) {
        final JsonObject content;
        try {
            content = new JsonObject(ioString(filename, null));
        } catch (final Throwable ex) {
            throw new _11004Exception415JsonFormat(filename);
        }
        return content;
    }

    static JsonObject ioJObject(final InputStream in) {
        final JsonObject content;
        try {
            content = new JsonObject(ioString(in, null));
        } catch (final Throwable ex) {
            throw new _11004Exception415JsonFormat("Stream/JObject");
        }
        return content;
    }

    static JsonObject ioJObject(final URL url) {
        if (Objects.isNull(url)) {
            return new JsonObject();
        }
        return Fn.jvmAs(
            url::openStream, Io::ioJObject,
            () -> new _11002Exception500EmptyIo("URL/JObject: " + url.getPath())
        );
    }


    static String ioString(final URL url, final String joined) {
        if (Objects.isNull(url)) {
            return VString.EMPTY;
        }
        return Fn.jvmAs(
            url::openStream, in -> ioString(in, joined),
            () -> new _11002Exception500EmptyIo("URL/String: " + url.getPath())
        );
    }

    static String ioString(final InputStream in, final String joined) {
        final StringBuilder buffer = new StringBuilder();
        return Fn.jvmOr(() -> {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                // Character stream
                String line;
                while (null != (line = reader.readLine())) {
                    buffer.append(line);
                    if (!TIs.isNil(joined)) {
                        buffer.append(joined);
                    }
                }
                return buffer.toString().trim();            // 追加一个 trim 去掉完整格式中前后空白
            }
        });
    }

    static String ioString(final String filename, final String joined) {
        return ioString(IoStream.read(filename), joined);
    }

    /**
     * Read to property object
     *
     * @param filename input filename
     *
     * @return Properties that will be returned
     */
    static Properties ioProp(final String filename) {
        return Fn.jvmOr(() -> {
            try (final InputStream in = IoStream.read(filename)) {
                final Properties prop = new Properties();
                prop.load(in);
                return prop;
            }
        });
    }

    static URL ioURL(final String filename) {
        return Fn.jvmOr(() -> {
            final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(filename);
            if (Objects.isNull(url)) {
                return Io.class.getResource(filename);
            } else {
                return url;
            }
        });
    }


    static File ioFile(final String filename) {
        final File file = new File(filename);
        if (file.exists()) {
            return file;
        }
        final URL url = ioURL(filename);
        if (null == url) {
            throw new _11002Exception500EmptyIo(filename);
        }
        return new File(url.getFile());
    }

    static boolean isExist(final String filename) {
        try {
            final File file = new File(filename);
            if (file.exists()) {
                // 截断处理
                return true;
            }
            final URL url = ioURL(filename);
            return isExist(url);
        } catch (final Throwable ex) {
            // Fix: java.lang.NullPointerException
            // File does not exist
            return false;
        }
    }

    static boolean isExist(final URL url) {
        if (Objects.isNull(url)) {
            return false;
        }
        if (url.getPath().contains(".jar!")) {
            // jar 文件检查
            return Fn.jvmOr(() -> {
                final JarURLConnection connection = (JarURLConnection) url.openConnection();
                return Objects.nonNull(connection.getJarFile());
            });
        } else {
            // 非 jar 文件检查
            try {
                url.openStream();
                return true;
            } catch (final IOException ex) {
                return false;
            }
        }
    }

    /**
     * Read to Path
     *
     * @param filename input filename
     *
     * @return file content that converted to String
     */
    static String ioPath(final String filename) {
        final File file = ioFile(filename);
        return file.getAbsolutePath();
    }
}
