package io.zerows.extension.skeleton.boot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 扩展修改器，用于自动修改 jOOQ 生成的 Zdb.java 文件。
 * 主要功能是：
 * 1. 添加对 Ke 工具类的导入。
 * 2. 将数据库模式名从硬编码 "ZDB" 替换为 Ke.getDatabase() 动态获取。
 * <p>
 * 注意：此类直接修改源文件，请谨慎使用并做好备份。
 * </p>
 *
 * @author lang : 2025-11-06
 */
@Slf4j // Lombok 注解，自动生成 private static final Logger info = LoggerFactory.getLogger(ExtensionModifier.class);
public class ExtensionModifier {

    // --- 配置常量 ---

    /** 要添加的 Ke 类导入语句 */
    private static final String KE_IMPORT_STATEMENT = "import io.zerows.extension.skeleton.common.Ke;";

    /** 硬编码的旧模式名 */
    private static final String OLD_SCHEMA_LITERAL = "\"ZDB\"";

    /** 新的动态获取模式名的表达式 */
    private static final String NEW_SCHEMA_EXPRESSION = "Ke.getDatabase()";

    /** 默认使用的文件字符集 */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    // --- 公共接口方法 ---

    /**
     * 修改指定路径的 Zdb.java 文件。
     * <ol>
     *   <li>添加对 {@code io.zerows.extension.skeleton.common.Ke} 的导入语句。</li>
     *   <li>将构造函数调用中的硬编码模式名 "ZDB" 替换为 {@code Ke.getDatabase()}。</li>
     * </ol>
     * 修改后的内容会写回到原文件中。
     *
     * @param filePath Zdb.java 文件的相对或绝对路径（例如："defaulta/Zdb.java"）。
     *
     * @return 如果文件被成功修改或已符合要求则返回 true；如果因任何原因（IO错误、格式不符等）未能修改则返回 false。
     */
    public static boolean modifyZdbFile(final String filePath) {
        log.info("开始尝试修改 Zdb 文件: {}", filePath);
        if (filePath == null || filePath.trim().isEmpty()) {
            log.warn("提供的文件路径为空或无效: '{}'", filePath);
            return false;
        }

        final File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            log.error("目标文件不存在: {}", filePath);
            return false;
        }
        if (!targetFile.isFile()) {
            log.error("指定路径不是一个有效文件: {}", filePath);
            return false;
        }

        try {
            // 1. 读取文件内容
            final String originalCode = FileUtils.readFileToString(targetFile, DEFAULT_CHARSET);
            if (originalCode == null || originalCode.isEmpty()) {
                log.warn("读取到的文件内容为空: {}", filePath);
                return false; // 认为不需要或无法修改空文件
            }

            // 2. 检查是否已经修改过了（避免重复修改和不必要的写入）
            if (isAlreadyModified(originalCode)) {
                log.info("文件 '{}' 已经符合修改要求，无需再次修改。", filePath);
                return true; // 视为成功
            }

            // 3. 应用修改逻辑
            final String modifiedCode = applyModifications(originalCode, filePath);
            if (modifiedCode == null) {
                // applyModifications 内部已记录具体失败原因
                return false;
            }

            // 4. 检查是否有实际变更
            if (originalCode.equals(modifiedCode)) {
                log.info("分析后发现文件 '{}' 内容无需更改。", filePath);
                // 即使无变更，也认为是成功的
                return true;
            }

            // 5. 将修改后的内容写回文件
            FileUtils.writeStringToFile(targetFile, modifiedCode, DEFAULT_CHARSET);
            log.info("成功修改并保存文件: {}", filePath);
            return true;

        } catch (final IOException e) {
            log.error("处理文件 '{}' 时发生 I/O 错误: {}", filePath, e.getMessage(), e);
        } catch (final Exception e) { // 捕获所有其他未预期的异常
            log.error("处理文件 '{}' 时发生未知错误: {}", filePath, e.getMessage(), e);
        }

        return false; // 默认返回失败
    }


    // --- 核心私有逻辑方法 ---

    /**
     * 判断文件内容是否已经符合修改后的要求。
     * （即已包含 Ke 导入且构造函数中已使用 Ke.getDatabase()）
     *
     * @param code 文件内容字符串。
     *
     * @return 如果已符合要求返回 true，否则返回 false。
     */
    private static boolean isAlreadyModified(final String code) {
        return code.contains(KE_IMPORT_STATEMENT) &&
            code.contains(NEW_SCHEMA_EXPRESSION) &&
            !code.contains("DSL.name(" + OLD_SCHEMA_LITERAL + ")");
    }

    /**
     * 修改 Zdb.java 源代码字符串的核心逻辑。
     *
     * @param originalCode   Zdb.java 文件的原始内容字符串。
     * @param sourceFilePath 源文件的路径（用于日志/错误报告）。
     *
     * @return 修改后的内容字符串，如果修改失败则返回 null。
     */
    private static String applyModifications(final String originalCode, final String sourceFilePath) {
        String workingCode = originalCode;
        boolean hasError = false;
        final StringBuilder errorMessageBuilder = new StringBuilder();

        try {
            // --- 修改 1: 添加 Ke 类的导入语句 ---
            if (!workingCode.contains(KE_IMPORT_STATEMENT)) {
                log.debug("文件 '{}' 缺少 Ke 导入，正在尝试添加...", sourceFilePath);
                final int insertPosition = findImportInsertPosition(workingCode);
                if (insertPosition >= 0) {
                    // 在插入点添加导入语句和换行符
                    workingCode = new StringBuilder(workingCode)
                        .insert(insertPosition, KE_IMPORT_STATEMENT + "\n")
                        .toString();
                    log.debug("已在文件 '{}' 中添加 Ke 导入语句。", sourceFilePath);
                } else {
                    final String errorMsg = "无法确定 Ke 导入语句 '" + KE_IMPORT_STATEMENT + "' 的插入位置。";
                    log.error(errorMsg + " 文件: {}", sourceFilePath);
                    errorMessageBuilder.append(errorMsg).append(" ");
                    hasError = true; // 标记错误，但继续尝试其他修改
                }
            } else {
                log.debug("文件 '{}' 已包含 Ke 导入语句。", sourceFilePath);
            }

            // --- 修改 2: 替换硬编码的模式名 ---
            if (workingCode.contains("DSL.name(" + OLD_SCHEMA_LITERAL + ")")) {
                log.debug("文件 '{}' 包含硬编码模式名，正在尝试替换...", sourceFilePath);
                workingCode = replaceSchemaName(workingCode, sourceFilePath, errorMessageBuilder);
                if (errorMessageBuilder.length() > 0) {
                    hasError = true; // replaceSchemaName 内部已记录详细错误
                } else {
                    log.debug("已在文件 '{}' 中替换硬编码模式名为动态获取。", sourceFilePath);
                }
            } else if (workingCode.contains("DSL.name(" + NEW_SCHEMA_EXPRESSION + ")")) {
                log.debug("文件 '{}' 的模式名已是动态获取形式。", sourceFilePath);
            } else {
                final String warnMsg = "文件 '" + sourceFilePath + "' 中未找到预期的 'DSL.name(...)' 模式名调用。";
                log.warn(warnMsg);
                // 不一定算错，取决于具体情况，这里暂不标记 hasError
            }


        } catch (final Exception e) {
            final String errorMsg = "应用修改时发生意外错误: " + e.getMessage();
            log.error(errorMsg + " 文件: {}", sourceFilePath, e);
            errorMessageBuilder.append(errorMsg).append(" ");
            hasError = true;
        }

        // --- 最终结果判断 ---
        if (hasError) {
            log.error("修改文件 '{}' 失败。详细信息: {}", sourceFilePath, errorMessageBuilder.toString().trim());
            return null; // 返回 null 表示失败
        }

        return workingCode; // 返回可能已被修改的代码
    }

    /**
     * 在代码中查找并替换硬编码的模式名。
     * 更专注于定位和替换逻辑。
     *
     * @param code           原始代码。
     * @param sourceFilePath 文件路径（用于日志）。
     * @param errorBuffer    用于收集此操作中发生的错误消息。
     *
     * @return 替换后的代码，如果失败则原始代码不变。
     */
    private static String replaceSchemaName(final String code, final String sourceFilePath, final StringBuilder errorBuffer) {
        String result = code;
        // 定位特定的构造函数
        final String constructorSignatureRegex = "private\\s+Zdb\\s*\\(\\s*\\)\\s*\\{";
        final Pattern constructorPattern = Pattern.compile(constructorSignatureRegex);
        final Matcher constructorMatcher = constructorPattern.matcher(result);

        if (constructorMatcher.find()) {
            final int constructorStartIndex = constructorMatcher.end(); // '{' 字符之后的索引

            // 查找构造函数体对应的闭合大括号 '}'
            final int constructorEndIndex = findMatchingClosingBrace(result, constructorStartIndex);
            if (constructorEndIndex != -1) {
                // 提取构造函数体
                final String constructorBody = result.substring(constructorStartIndex, constructorEndIndex);

                // 在构造函数体内部查找需要替换的 super 调用
                // 使用非贪婪匹配 .*? 来适应 super 调用的不同形式
                final String oldSchemaCall = "DSL\\.name\\s*\\(\\s*" + OLD_SCHEMA_LITERAL + "\\s*\\)";
                final String replacementSchemaCall = "DSL.name(" + NEW_SCHEMA_EXPRESSION + ")";
                // 注意：replaceAll 的第一个参数是 regex，需要转义；第二个是 literal replacement
                final String modifiedConstructorBody = constructorBody.replaceAll(oldSchemaCall, replacementSchemaCall);

                // 如果替换后内容相同，说明没找到匹配项
                if (modifiedConstructorBody.equals(constructorBody)) {
                    final String errorMsg = "在构造函数体内未找到匹配的 '" + OLD_SCHEMA_LITERAL + "' 调用以进行替换。";
                    log.warn(errorMsg + " 文件: {}", sourceFilePath);
                    errorBuffer.append(errorMsg).append(" ");
                    // 不算致命错误，返回原代码
                } else {
                    // 使用修改后的构造函数体重建整个文件内容
                    result = result.substring(0, constructorStartIndex) +
                        modifiedConstructorBody +
                        result.substring(constructorEndIndex);
                }

            } else {
                final String errorMsg = "找不到 Zdb() 构造函数的闭合大括号 '}'。";
                log.error(errorMsg + " 文件: {}", sourceFilePath);
                errorBuffer.append(errorMsg).append(" ");
            }
        } else {
            final String errorMsg = "找不到 'private Zdb()' 构造函数定义。";
            log.error(errorMsg + " 文件: {}", sourceFilePath);
            errorBuffer.append(errorMsg).append(" ");
        }
        return result;
    }


    /**
     * 查找应该插入新导入语句的索引位置。
     * 尝试找到最后一个现有导入语句的末尾。
     * 如果没有导入语句，则回退到包声明之后插入。
     *
     * @param code 源代码字符串。
     *
     * @return 插入新导入语句的索引（字符位置），如果找不到合适的地方则返回 -1。
     */
    private static int findImportInsertPosition(final String code) {
        // 尝试查找最后一个现有的 import
        final Pattern importPattern = Pattern.compile("^import\\s+[\\w\\.]+;\\s*$", Pattern.MULTILINE);
        final Matcher importMatcher = importPattern.matcher(code);

        int lastImportEnd = -1;
        while (importMatcher.find()) {
            lastImportEnd = importMatcher.end(); // 持续更新到最后一个找到的位置
        }

        if (lastImportEnd > 0) {
            return lastImportEnd; // 在最后一个导入之后插入
        }

        // 如果没有找到导入，尝试在 package 声明之后插入
        final Pattern packagePattern = Pattern.compile("^package\\s+[\\w\\.]+;\\s*$", Pattern.MULTILINE);
        final Matcher packageMatcher = packagePattern.matcher(code);
        if (packageMatcher.find()) {
            return packageMatcher.end(); // 在 package 行之后插入
        }

        // 如果两者都找不到，则不知道该放在哪里
        log.warn("无法找到合适的导入插入点 (既无现有导入也无 package 声明)");
        return -1;
    }

    /**
     * 辅助方法，用于查找与给定索引处开始的块相对应的闭合大括号 '}'。
     * 假设起始索引紧跟在开头大括号 '{' 之后。
     *
     * @param code       源代码字符串。
     * @param startIndex 开头大括号 '{' 之后的索引。
     *
     * @return 对应的闭合大括号 '}' 的索引，如果未找到则返回 -1。
     */
    private static int findMatchingClosingBrace(final String code, final int startIndex) {
        int braceCount = 1; // 我们从第一个 '{' 之后开始，所以计数为 1
        for (int i = startIndex; i < code.length(); i++) {
            final char c = code.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i; // 找到了匹配的闭合大括号
                }
            }
            // 可以选择添加对字符串/注释的检查以忽略其中的大括号，
            // 但对于像 jOOQ 这样的生成代码，通常可以安全地跳过。
        }
        return -1; // 未找到
    }

    // --- 示例 Main 方法 (仅供测试) ---
    // 集成到构建过程前请记得移除或注释掉
    /*
    public static void main(String[] args) {
        String testFilePath = "defaulta/Zdb.java"; // 根据需要调整测试路径

        boolean success = ExtensionModifier.modifyZdbFile(testFilePath);
        if (success) {
            System.out.println("文件修改成功或已符合要求。");
        } else {
            System.err.println("文件修改失败。");
        }
    }
    */
}


