package io.zerows.ams.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.enums.typed.EmType;
import io.zerows.core.exception.boot._11002Exception500EmptyIo;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * @author lang : 2023-06-16
 */
class IoYaml {
    private static final ObjectMapper YAML = new YAMLMapper();

    private IoYaml() {
    }

    static <T> T ioYaml(final String filename) {
        if (TIs.isNil(filename)) {
            return null;
        }
        final InputStream in = IoStream.read(filename);
        final String literal = ioYamlNode(in, filename).toString();
        return ioYamlResult(literal);
    }

    static <T> T ioYaml(final InputStream in) {
        if (Objects.isNull(in)) {
            return null;
        }
        final String literal = ioYamlNode(in, "Stream/Yaml").toString();
        return ioYamlResult(literal);
    }

    static <T> T ioYaml(final URL url) {
        if (Objects.isNull(url)) {
            return null;
        }
        return Fn.jvmAs(
            url::openStream, IoYaml::ioYaml,
            () -> new _11002Exception500EmptyIo("URL/Yaml: " + url.getPath())
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T ioYamlResult(final String literal) {
        if (TIs.isNil(literal)) {
            /*
             * 若 content 是 null 或 empty，返回 null 引用
             */
            return null;
        }
        final EmType.Yaml type = ioYamlType(literal);
        if (EmType.Yaml.ARRAY == type) {
            return (T) new JsonArray(literal);
        } else {
            return (T) new JsonObject(literal);
        }
    }

    @SuppressWarnings("all")
    private static JsonNode ioYamlNode(final InputStream in, final String filename) {
        final JsonNode node = Fn.jvmOr(() -> {
            if (null == in) {
                throw new _11002Exception500EmptyIo(filename);
            }
            return YAML.readTree(in);
        });
        if (null == node) {
            throw new _11002Exception500EmptyIo(filename);
        }
        return node;
    }

    private static EmType.Yaml ioYamlType(final String content) {
        if (Objects.isNull(content)) {
            return EmType.Yaml.OBJECT;
        }
        final String contentYaml = content.trim();
        if (contentYaml.startsWith(VString.DASH) ||         // ioString 结果
            contentYaml.startsWith(VString.LEFT_SQUARE)     // readTree 结果
        ) {
            return EmType.Yaml.ARRAY;
        } else {
            return EmType.Yaml.OBJECT;
        }
    }
}
