package io.zerows.support.base;

import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2023/4/28
 */
class IoPath {

    static String resolve(final String path, final Environment environment) {
        if (TIs.isNil(path) || path.startsWith(VString.SLASH)) {
            return path;
        }
        if (Environment.Production == environment) {
            return path;
        } else {
            // TODO: 追加环境变量，默认用了 Maven 的
            return resolve("src/main/resources", path);
        }
    }

    static String resolve(final String folder, final String file) {
        Objects.requireNonNull(file, "File path cannot be null");

        // 1. 规范化 folder
        final String base = (folder == null) ? "" : folder.trim().replace("\\", "/");

        // 2. 规范化 file (去除首部的斜杠，因为我们要手动控制拼接)
        String append = file.trim().replace("\\", "/");
        while (append.startsWith("/")) {
            append = append.substring(1);
        }

        if (base.isEmpty()) {
            return append;
        }

        // 3. 智能处理重叠部分 (如 base="usr/local", append="local/bin")
        // 这里保持您之前的逻辑，但去掉 base 结尾的斜杠
        final String cleanBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;

        final String[] baseParts = cleanBase.split("/");
        if (baseParts.length > 0) {
            final String lastSegment = baseParts[baseParts.length - 1];
            if (append.startsWith(lastSegment + "/")) {
                append = append.substring(lastSegment.length() + 1);
            } else if (append.equals(lastSegment)) {
                append = "";
            }
        }

        // 4. 最终拼接：不再强制在最前面加 "/"
        if (append.isEmpty()) {
            return cleanBase;
        }
        return cleanBase + "/" + append;
    }


    static List<String> ladder(final String path) {
        if (TIs.isNil(path)) {
            return new ArrayList<>();
        }
        final String[] splitArr = path.split(VString.SLASH);
        final List<String> itemList = new ArrayList<>();
        for (int idx = 0; idx < splitArr.length; idx++) {
            final StringBuilder item = new StringBuilder();
            for (int jdx = 0; jdx < idx; jdx++) {
                item.append(splitArr[jdx]).append(VString.SLASH);
            }
            item.append(splitArr[idx]);
            final String itemStr = item.toString();
            if (!TIs.isNil(itemStr)) {
                itemList.add(itemStr);
            }
        }
        return itemList;
    }

    static String first(final String path, final String separator) {
        final String[] names = path.split(separator);
        String result = null;
        for (final String found : names) {
            if (UtBase.isNotNil(found)) {
                result = found;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("all")
    static String last(final String path, final String separator) {
        final String[] names = path.split(separator);
        String result = null;
        for (int idx = names.length - 1; idx < names.length; idx--) {
            final String found = names[idx];
            if (UtBase.isNotNil(found)) {
                result = found;
                break;
            }
        }
        return result;
    }
}
