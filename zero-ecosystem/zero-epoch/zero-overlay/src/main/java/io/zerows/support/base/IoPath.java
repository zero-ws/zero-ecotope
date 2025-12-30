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

        // 3. 去掉 base 结尾的斜杠
        final String cleanBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;

        // 4. 智能去重：检测并移除真正的路径重叠
        // 例如：base="usr/local/bin", append="local/bin/test" 
        //      找到最长重叠 "local/bin"，结果为 "usr/local/bin/test"
        // 例如：base="a/rule.menu", append="rule.menu/config"
        //      只有一段重叠 "rule.menu"，但这是合法的同名目录，需要保留
        final String[] baseParts = cleanBase.split("/");
        final String[] appendParts = append.split("/");
        
        // 从 base 的末尾和 append 的开头找最长连续匹配段数
        int maxOverlap = getMaxOverlap(baseParts, appendParts);

        // 只有当重叠段数 >= 2 时才去重，避免误判单个同名目录
        // 例如：base="a/b/c", append="b/c/d" 重叠2段 "b/c" → 去重 ✓
        // 例如：base="a/rule.menu", append="rule.menu/c" 重叠1段 → 不去重 ✓
        if (maxOverlap >= 2) {
            final StringBuilder newAppend = new StringBuilder();
            for (int i = maxOverlap; i < appendParts.length; i++) {
                if (!newAppend.isEmpty()) {
                    newAppend.append("/");
                }
                newAppend.append(appendParts[i]);
            }
            append = newAppend.toString();
        }

        // 5. 最终拼接
        if (append.isEmpty()) {
            return cleanBase;
        }
        return cleanBase + "/" + append;
    }

    private static int getMaxOverlap(String[] baseParts, String[] appendParts) {
        int maxOverlap = 0;
        for (int overlapLen = 1; overlapLen <= Math.min(baseParts.length, appendParts.length); overlapLen++) {
            boolean allMatch = true;
            for (int i = 0; i < overlapLen; i++) {
                if (!baseParts[baseParts.length - overlapLen + i].equals(appendParts[i])) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                maxOverlap = overlapLen;
            }
        }
        return maxOverlap;
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
