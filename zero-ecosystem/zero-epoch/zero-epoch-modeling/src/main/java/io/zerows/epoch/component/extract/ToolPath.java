package io.zerows.epoch.component.extract;

import io.r2mo.function.Fn;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.component.environment.DevEnv;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.corpus.exception._40006Exception500PathAnnoEmpty;
import io.zerows.epoch.program.Ut;
import jakarta.ws.rs.Path;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Path resolver
 * 1. Root ( Class ) + Context ( Method )
 * 2. Context ( Method )
 */
class ToolPath {

    private static final Annal LOGGER = Annal.get(ToolPath.class);

    /**
     * Parse the api endpoint for @Path ( Class Level )
     *
     * @param path JSR311 annotation
     *
     * @return normalized uri
     */
    public static String resolve(final Path path) {
        // Calculate single path
        Fn.jvmKo(Objects.isNull(path), _40006Exception500PathAnnoEmpty.class);
        return resolve(path, null);
    }

    /**
     * Parse the api endpoint for @Path ( Method Level )
     *
     * @param path JSR311 annotation
     * @param root root folder or path
     *
     * @return normalized uri
     */
    @SuppressWarnings("all")
    public static String resolve(final Path path, final String root) {
        Fn.jvmKo(Objects.isNull(path), _40006Exception500PathAnnoEmpty.class);
        if (Ut.isNil(root)) {
            return calculate(path(path.value()));
        }
        final String api = calculate(root);
        final String contextPath = calculate(path.value());
        // If api has been calculated to
        return VValue.ONE == api.length() ?
            path(contextPath) : path(api + contextPath);
    }

    /**
     * JSR311: /query/{name}
     * Named: /query/:name ( Vertx Format )
     *
     * @param path JSR311 annotation
     *
     * @return resolved Vert.x and JSR311
     */
    @SuppressWarnings("all")
    private static String path(final String path) {
        final String regex = "\\{\\w+\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(path);
        String tempStr = path;
        String result = "";
        while (matcher.find()) {
            result = matcher.group();
            // Shift left brace and right brace
            final String replaced = result.trim().substring(1, result.length() - 1);
            tempStr = tempStr.replace(result, ":" + replaced);
        }
        return tempStr;
    }

    /**
     * Calculate the path
     * 1. Remove the last '/';
     * 2. Append the '/' to first;
     * 3. Replaced all duplicated '//';
     *
     * @param path input path no normalized.
     *
     * @return calculated uri
     */
    @SuppressWarnings("all")
    private static String calculate(final String path) {
        String uri = path;
        // 1. Shift the SLASH: Multi -> Single one.
        uri = uri.replaceAll("\\/+", VString.SLASH);
        // 1. Remove the last SLASH
        if (uri.endsWith(VString.SLASH)) {
            uri = uri.substring(0, uri.lastIndexOf(VString.SLASH));
        }
        // Uri must begin with SLASH
        final String processed = uri;
        final String finalUri = processed.startsWith(VString.SLASH) ? processed : VString.SLASH + processed;
        if (!path.equals(finalUri) && DevEnv.devWebUri()) {
            LOGGER.warn("[ Path ] The original uri is `{0}`, recommend/detected uri is `{1}`.", path, finalUri);
        }
        return finalUri;
    }
}
