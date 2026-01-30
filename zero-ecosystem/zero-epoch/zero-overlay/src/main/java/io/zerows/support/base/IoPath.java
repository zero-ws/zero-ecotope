package io.zerows.support.base;

import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.Environment;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 解析并拼接路径，支持多个片段，自动处理斜杠和重复片段
     *
     * @param folder   基础路径
     * @param segments 后续路径片段 (e.g., "api", "/user/get", "info")
     * @return 规范化后的完整路径
     */
    public static String resolve(final String folder, final String... segments) {
        // 1. 初始化基础路径 (base)
        String currentPath = (folder == null) ? "" : folder.trim().replace("\\", "/");

        // 如果没有后续片段，直接处理返回
        if (segments == null || segments.length == 0) {
            // 去除末尾斜杠（保持统一风格，可选）
            return currentPath.endsWith("/") && currentPath.length() > 1 ?
                currentPath.substring(0, currentPath.length() - 1) : currentPath;
        }

        // 2. 循环处理每一个片段
        for (final String segment : segments) {
            if (segment == null || segment.trim().isEmpty()) {
                continue;
            }

            // --- 下面是您原有逻辑的复用与适配 ---

            // A. 规范化当前片段 (append)
            String append = segment.trim().replace("\\", "/");

            // 去除 append 首部的斜杠 (为了后续拼接)
            while (append.startsWith("/")) {
                append = append.substring(1);
            }
            // 去除 append 尾部的斜杠 (防止拼接后出现 // 或者影响下一次重叠判断)
            while (append.endsWith("/")) {
                append = append.substring(0, append.length() - 1);
            }

            // 如果 currentPath 为空，直接赋值
            if (currentPath.isEmpty()) {
                currentPath = append;
                continue;
            }

            // B. 准备 currentPath (去除末尾斜杠以便进行重叠判断)
            // 注意：根路径 "/" 特殊处理
            String cleanBase = currentPath;
            if (cleanBase.endsWith("/") && cleanBase.length() > 1) {
                cleanBase = cleanBase.substring(0, cleanBase.length() - 1);
            }

            // C. 智能处理重叠部分 (如 base="api/v1", append="v1/user")
            // 获取 base 的最后一段
            final int lastSlashIndex = cleanBase.lastIndexOf('/');
            final String lastSegment = (lastSlashIndex == -1) ? cleanBase : cleanBase.substring(lastSlashIndex + 1);

            // 检查重叠
            if (append.startsWith(lastSegment + "/")) {
                // 情况 1: append = "xxx/yyy", lastSegment = "xxx" -> 截取掉 "xxx/"
                append = append.substring(lastSegment.length() + 1);
            } else if (append.equals(lastSegment)) {
                // 情况 2: append = "xxx", lastSegment = "xxx" -> 变成空
                append = "";
            }

            // D. 拼接更新 currentPath
            if (!append.isEmpty()) {
                // 如果 base 是 "/"，拼接时不需要加额外的 "/" (变成 //api)
                if (cleanBase.equals("/")) {
                    currentPath = cleanBase + append;
                } else {
                    currentPath = cleanBase + "/" + append;
                }
            } else {
                currentPath = cleanBase;
            }
        }

        return currentPath;
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
