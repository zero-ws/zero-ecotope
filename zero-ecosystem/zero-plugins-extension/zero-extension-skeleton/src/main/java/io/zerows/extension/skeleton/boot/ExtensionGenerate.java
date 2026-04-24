package io.zerows.extension.skeleton.boot;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.io.HStore;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.r2mo.vertx.jooq.generate.JooqSourceConfiguration;
import io.r2mo.vertx.jooq.generate.JooqSourceConfigurer;
import io.r2mo.vertx.jooq.generate.VertxGeneratorStrategy;
import io.r2mo.vertx.jooq.generate.classic.ClassicJDBCVertxGenerator;
import io.r2mo.vertx.jooq.generate.configuration.MetaGenerate;
import io.r2mo.vertx.jooq.generate.configuration.MetaSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 注意，代码生成过程中最终输出的数据库名称为 ZDB（此处），而真正使用过程中
 * <pre>
 *     1. 直接跑编程模式开发新模块，此处数据库应该 = {@link Database#getInstance()}
 *     2. 研发 Zero 扩展模块，此处固定值是 ZDB，运行时会被替换为真实数据库
 * </pre>
 *
 * @author lang : 2025-10-20
 */
@Slf4j
class ExtensionGenerate {

    private static final HStore STORE = SPI.V_STORE;
    private static final JUtil UT = SPI.V_UTIL;

    private static Path resolve() {
        // 1) 明确指定优先
        final String explicit = System.getProperty("basedir");
        if (explicit != null && !explicit.isBlank()) {
            return Paths.get(explicit).toAbsolutePath().normalize();
        }

        // 2) Maven 提供（根目录）
        final String mm = System.getProperty("maven.multiModuleProjectDirectory");
        if (mm != null && !mm.isBlank()) {
            return Paths.get(mm).toAbsolutePath().normalize();
        }

        final String env = System.getenv("MAVEN_PROJECTBASEDIR");
        if (env != null && !env.isBlank()) {
            return Paths.get(env).toAbsolutePath().normalize();
        }

        // 3) 回退：从当前目录一路向上找 pom.xml，取“最上层”的那个
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path topMostWithPom = null;
        while (dir != null) {
            if (Files.exists(dir.resolve("pom.xml"))) {
                topMostWithPom = dir; // 继续往上，拿“最上层” pom
            }
            dir = dir.getParent();
        }
        if (topMostWithPom != null) {
            return topMostWithPom;
        }

        // 4) 兜底：当前工作目录
        return Paths.get(".").toAbsolutePath().normalize();
    }

    void start(final MetaGenerate program, final String[] args) {
        // 提取配置信息，执行代码生成
        Objects.requireNonNull(program, "[ PLUG ] 代码生成配置不可为 null.");
        final Class<?> clazz = program.getClass();
        log.info("[ PLUG ] 代码生成配置类：{}", clazz.getName());
        final URL url = this.loadUrl(clazz, "vertx-generate.yml");
        log.info("[ PLUG ] 加载配置文件：{}", url);

        // 根据配置直接提取 Yaml
        final JObject sourceYaml = STORE.inYaml(url);
        final MetaGenerate configured = UT.deserializeJson(sourceYaml, MetaGenerate.class);


        // 合并程序部分和配置部分
        final MetaGenerate generate = configured.merge(program);

        if (Objects.isNull(generate)) {
            log.error("[ PLUG ] 代码生成配置解析失败，程序终止，vertx-of.yml 文件缺失！！");
            System.exit(-1);
        }
        final MetaSource source = generate.getSource();
        Objects.requireNonNull(source, "[ PLUG ] 代码生成源代码配置不可为 null.");
        if (Objects.isNull(source.getDirectory())) {
            source.setDirectory(this.findMavenSourceDirectory(clazz));
        }
        log.info("[ PLUG ] Maven 源代码目录：{}", source.getDirectory());

        if (Objects.isNull(generate.getDatabase())) {
            log.error("[ PLUG ] 代码生成数据库配置解析失败！");
            System.exit(-1);
        }


        // Strategy 配置
        final JooqSourceConfiguration configuration = new JooqSourceConfiguration();
        configuration.database(generate.getDatabase());         // 数据库
        configuration.directory(source.getDirectory());         // 输出目录

        BeanUtil.copyProperties(source, configuration,
            CopyOptions.create()
                .ignoreError()
                .ignoreNullValue()
        );

        if (Objects.isNull(source.getClassStrategy())) {
            configuration.classStrategy(VertxGeneratorStrategy.class);
        }
        if (Objects.isNull(source.getClassGenerator())) {
            configuration.classGenerator(ClassicJDBCVertxGenerator.class);
        }

        if (Objects.nonNull(generate.resolver())) {
            configuration.resolver(generate.resolver());
        }


        configuration.classPackage(clazz.getPackage());
        configuration.databaseIncludes(source.getIncludes());
        configuration.databaseExcludes(source.getExcludes());

        // 构造 Jooq 的标准代码生成配置
        log.info("[ PLUG ] 配置程序预处理……");
        log.info("[ PLUG ] Includes 规则：{}", source.getIncludes());
        log.info("[ PLUG ] Excludes 规则：{}", source.getExcludes());
        log.info("[ PLUG ] Strategy 类：{}", configuration.classStrategy().getName());
        log.info("[ PLUG ] Generator 类：{}", configuration.classGenerator().getName());
        final Configuration compiled = JooqSourceConfigurer.of().configure(configuration);
        log.info("[ PLUG ] 生成核心代码");
        try {
            GenerationTool.generate(compiled);
            log.info("[ PLUG ] 注：如果是 Zero Extension 扩展模块开发，请检查 Zdb.java 文件中的数据库名称！");
            this.writeZDB(configuration);
            log.info("[ PLUG ] 您的数据库相关代码生成成功！");
        } catch (final Throwable ex) {
            // ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }
    }

    private void writeZDB(final JooqSourceConfiguration configuration) {
        final String packageName = configuration.classPackage().getName();
        final String zdbPath = configuration.directory() +
            File.separator + packageName.replace('.', File.separatorChar) +
            File.separator + "domain" +
            File.separator + "Zdb.java";
        ExtensionModifier.modifyZdbFile(zdbPath);
    }

    /**
     * 🔍 从类路径向上查找 Maven 标准源代码目录
     *
     * @param clazz 参考类
     * @return Maven 源代码目录 (src/main/java/)
     */
    public String findMavenSourceDirectory(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        try {
            // 获取类的资源路径
            final String resourceName = clazz.getName().replace('.', '/') + ".class";
            final String classPath = Objects.requireNonNull(clazz.getClassLoader().getResource(resourceName)).getPath();
            // Windows 下 URL#getPath() 可能返回 "/H:/xxx"，导致 Paths.get(...) 报错 Illegal char <:> at index 2
            final String normalizedPath = (classPath != null && classPath.length() > 2 &&
                classPath.charAt(0) == '/' &&
                Character.isLetter(classPath.charAt(1)) &&
                classPath.charAt(2) == ':') ? classPath.substring(1) : classPath;

            // 获取类文件所在目录

            // 向上查找直到找到 pom.xml
            Path current = null;
            if (normalizedPath != null) {
                current = Paths.get(normalizedPath).getParent();
            }
            while (current != null) {
                final File pomFile = current.resolve("pom.xml").toFile();
                if (pomFile.exists()) {
                    // 找到 pom.xml，返回 src/main/java 目录
                    final Path sourceDir = current.resolve("src").resolve("main").resolve("java");
                    return sourceDir.toString();
                }

                current = current.getParent();
            }

        } catch (final Exception e) {
            // e.printStackTrace();
            log.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 🔍 优先从类所在 classpath 加载 URL，找不到时使用类加载器
     *
     * @param clazz        参考类
     * @param resourcePath 资源路径
     * @return URL 对象，如果找不到则返回 null
     */
    private URL loadUrl(final Class<?> clazz, final String resourcePath) {
        if (clazz == null || resourcePath == null) {
            return null;
        }

        // 🎯 优先从类所在位置加载
        URL url = clazz.getResource(resourcePath);
        if (url != null) {
            return url;
        }

        // 🔍 如果类位置找不到，使用类加载器加载
        final ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            url = classLoader.getResource(resourcePath);
            if (url != null) {
                return url;
            }
        }

        // 🔍 如果当前类加载器找不到，尝试系统类加载器
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null && !systemClassLoader.equals(classLoader)) {
            url = systemClassLoader.getResource(resourcePath);
            if (url != null) {
                return url;
            }
        }

        return null;
    }
}
