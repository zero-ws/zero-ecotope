package io.zerows.extension.crud.plugins;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.common.Ix;
import io.zerows.extension.skeleton.spi.ScOrbit;

import java.util.Set;

/**
 * 此处资源信息要重新改写后强化，计算资源 URI 的核心地址，以路径为主
 * <pre>
 *     BUG 01:
 *     - GET /api/day/book, uri = /api/:actor/:key
 *
 * </pre>
 */
public class ScOrbitCommon implements ScOrbit {

    private static final Set<String> URI_POST = Ix.uriPost();
    private static final Set<String> URI_PRE = Ix.uriPre();

    /**
     * <pre>
     *     {
     *         "uri":           "/api/:actor/search",
     *         "requestUri":    "/api/group/search"
     *     }
     * </pre>
     *
     * @param arguments 参数信息
     * @return 计算后的 URI 信息
     */
    @Override
    public String analyze(final JsonObject arguments) {
        final String uri = arguments.getString(ARG0);
        final String requestUri = arguments.getString(ARG1);

        /* Code Logical */
        if (this.isMatch(requestUri)) {
            final String[] source = uri.split("/");
            final String[] request = requestUri.split("/");
            /*
             * Scan for actor parameters
             */
            final StringBuilder builder = new StringBuilder();
            for (int idx = 0; idx < source.length; idx++) {
                if (":actor".equals(source[idx])) {
                    builder.append(request[idx]).append('/');
                } else {
                    builder.append(source[idx]).append('/');
                }
            }
            return builder.delete(builder.length() - 1, builder.length()).toString();
        } else {

            /*
             * 是否完整转换
             */
            if (URI_PRE.contains(uri)) {
                return requestUri;
            }
            return uri;
        }
    }

    private boolean isMatch(final String requestUri) {
        if (URI_POST.contains(requestUri)) {
            /* No :key mode */
            return true;
        } else {
            /* Length > 36, at least contains UUID */
            return 36 < requestUri.length();
        }
    }
}
