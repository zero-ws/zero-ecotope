package io.zerows.epoch.assembly;

/**
 * 说明：
 * 1) 此清单用于“前缀黑名单”（String.startsWith），命中即跳过扫描与反射；不删除任何原有项，仅分区+注释增强。
 * 2) 已按你的反馈补回：org.reactivestreams、ch.qos、org.aspectj、org.jcodings（与 org.joni 配套）。
 * 3) 建议对白名单（如 io.zerows.**）优先放行，结合本黑名单可进一步降低扫描量。
 * 新增标记（最近几次补全）：
 * - ch.qos（Logback 上游组织根包，含 ch.qos.logback）🪵
 * - org.reactivestreams（Reactive Streams 标准）🔁
 * - org.aspectj（AspectJ AOP/织入）🧭
 * - org.jcodings（JRuby 编码库，org.joni 搭档）🔤  ← 新增
 *
 * @author lang : 2024-04-17
 */
interface ClassFilterPackage {

    String[] SKIP_PACKAGE = new String[]{
        // =========================== 🧱 Core / JDK / META ===========================
        "IMPL-JARS",                   // 自定义/实现 Jar 占位命名 🧩
        "META-INF",                    // JAR 元数据/清单 📦
        "apache",                      // 非标准顶级“apache”包（历史遗留，兜底）🧯
        "java",                        // JDK 标准库（java.*）☕
        "com.azul",                    // Azul Zulu 专有包 🦎
        "java.util.concurrent",        // J.U.C（已被 java.* 覆盖，保留以稳健）⏱️
        "javax",                       // 旧 Javax API（向后兼容）🧷
        "jdk",                         // JDK 内部模块 ⚙️
        "sun",                         // Sun/Oracle 专有实现 🌞

        // =========================== 🏛️ Jakarta / EE API ===========================
        "jakarta",                     // Jakarta EE API（Servlet/JAX-RS/JPA/Validation…）🏛️

        // =========================== 🤖 Android / Shaded / Relocated ===========================
        "aj.org.objectweb",            // ASM 阴影/重定位（常见于 AspectJ）🧪
        "android.annotation",          // Android 注解（非服务端）🤖
        "camundafeel",                 // Camunda FEEL 表达式相关 🧠
        "camundajar",                  // Camunda shaded 包（Jar 内重定位）📦
        "nonapi",                      // 非公开 API 命名空间（shaded）🕳️
        "schemaorg_apache_xmlbeans",   // XMLBeans 阴影包（OOXML 相关，shaded）📦
        "org.apache.xmlbeans",         // XMLBeans 正牌包（非 shaded）📄

        // =========================== ☁️ Vendors / Cloud SDK ===========================
        "com.alibaba",                 // Alibaba 生态总根（Nacos/Sentinel/Cloud/fastjson2 等）🏷️
        "com.aliyun",                  // 阿里云 SDK ☁️
        "com.amazonaws",               // AWS SDK v1 ☁️
        "com.microsoft",               // Microsoft/Azure/Graph 等 SDK 🪟
        "com.oracle",                  // Oracle 相关（JDBC/SDK）🏛️
        "com.nimbusds",                // Nimbus JOSE+JWT 🔐
        "oracle",                      // Oracle 历史/别名包 🏛️
        "software.amazon.awssdk",      // AWS SDK v2 ☁️

        // =========================== 🔐 Security / Crypto / Auth ===========================
        "com.auth0",                   // Java JWT 🔐
        "org.aopalliance",             // AOP Alliance（拦截器等）🧩
        "org.bouncycastle",            // BouncyCastle 加密 🔐
        "org.conscrypt",               // Conscrypt TLS 提供者 🔒

        // =========================== 🌐 Networking / HTTP / NIO ===========================
        "com.netflix",                 // Netflix OSS（Feign/Hystrix/Archaius…）🎬
        "com.networknt",               // light-4j / JSON Schema Validator 🌐
        "com.squareup",                // OkHttp/Retrofit/Okio（部分 groupId）🧰
        "feign",                       // Netflix Feign（早期 groupId）📨
        "io.netty",                    // Netty 网络框架 🌐
        "io.termd",                    // termd 终端/TTY 🖥️
        "io.vertx",                    // Vert.x 栈 🧭
        "okhttp3",                     // OkHttp 3 🌐
        "okio",                        // Okio 🔗
        "org.apache.hc",               // HttpComponents 5 真实包（client5/core5）🛰️
        "org.apache.http",             // Apache HttpClient 4 🌐
        "org.apache.httpcomponents",   // Maven 组织名（httpcomponents），保留兼容 🗂️
        "retrofit2",                   // Retrofit 2 🔌

        // =========================== 🔭 Observability / RPC / Reactive ===========================
        "io.grpc",                     // gRPC 栈 📡
        "io.micrometer",               // Micrometer 指标 📈
        "io.opentelemetry",            // OpenTelemetry 观测 📊
        "io.opentracing",              // OpenTracing（旧）🕸️
        "io.perfmark",                 // PerfMark（gRPC 性能标记）🏷️
        "io.projectreactor",           // Reactor（新根包）⚛️
        "io.prometheus",               // Prometheus Java 客户端 📊
        "io.r2dbc",                    // R2DBC SPI/驱动 🔌
        "io.reactivex",                // RxJava（1.x 根包）⚛️
        "io.swagger",                  // Swagger/OpenAPI 工具链 📜
        "io.hawt",                     // Hawtio 控制台 🪟
        "org.reactivestreams",         // Reactive Streams 标准 🔁
        "org.jolokia",                 // Jolokia JMX-HTTP 桥 🔧
        "reactor",                     // Reactor 旧根（与 io.projectreactor 并存）♻️

        // =========================== 🪵 Logging ===========================
        "ch.qos",                      // Logback 上游组织根包（含 ch.qos.logback）🪵
        "ch.qos.logback",              // Logback 日志实现 📜
        "org.apache.logging.log4j",    // Log4j2 📜
        "org.slf4j",                   // SLF4J 日志门面 🪵

        // =========================== 🧰 Bytecode / Annotations / Build ===========================
        "cglib",                       // CGLIB 字节码生成 🧬
        "com.esotericsoftware",        // Kryo / ReflectASM 等 🧊
        "javassist",                   // Javassist 字节码库 🧬
        "kotlin",                      // Kotlin 标准库 🅺
        "lombok",                      // Lombok 编译期注解 🏷️
        "net.bytebuddy",               // ByteBuddy 字节码工具 🧬
        "org.aspectj",                 // AspectJ AOP/织入 🧭
        "org.checkerframework",        // Checker Framework 注解 ✅
        "org.codehaus",                // Codehaus 旧组织（Groovy/Janino 等）🏚️
        "org.codehaus.groovy",         // Groovy（脚本/AST）📜
        "org.jetbrains",               // JetBrains 注解/工具（@NotNull 等）✨
        "org.objectweb",               // OW2/ASM 旧组织名 🧱
        "org.reflections",             // Reflections 扫描库 🔍

        // =========================== 🗄️ Data / Drivers / ORM / Cache ===========================
        "cn.hutool",                   // Hutool 工具集 🧰
        "com.baomidou",                // MyBatis-Plus / 动态数据源 / MPJ 🧩
        "com.github.benmanes.caffeine",// Caffeine 缓存 ⚡
        "com.h2database",              // H2 数据库 🧪
        "com.hazelcast",               // Hazelcast 集群/缓存 🧩
        "com.mongodb",                 // MongoDB 驱动 🍃
        "com.mysql",                   // MySQL JDBC 驱动 🐬
        "com.zaxxer",                  // HikariCP 连接池 💧
        "de.javakaffee",               // Kryo-serializers 等 🧩
        "liquibase",                   // Liquibase（历史根包名）🗂️
        "io.lettuce",                  // Lettuce Redis 客户端 🥬
        "it.unimi.dsi.fastutil",       // fastutil 大集合库（体量大，强烈建议跳过）🧺
        "org.apache.commons.dbcp2",    // Apache DBCP2 连接池 💧
        "org.apache.derby",            // Apache Derby 数据库 🧱
        "org.ehcache",                 // Ehcache 缓存 🧊
        "org.flywaydb",                // Flyway 数据库迁移 🛫
        "org.hibernate",               // Hibernate ORM 🧭
        "org.infinispan",              // Infinispan 数据网格 🗺️
        "org.jooq",                    // jOOQ 类型安全 SQL 🧾
        "org.mongodb",                 // MongoDB 驱动（部分包）🍃
        "org.neo4j",                   // Neo4j（除 driver 外）🕸️
        "org.neo4j.driver",            // Neo4j 驱动 🕸️
        "org.derive4j",                // Derive4j（函数式编程）🎯
        "org.postgresql",              // PostgreSQL JDBC 驱动 🐘
        "org.redisson",                // Redisson Redis 客户端 🔴
        "redis",                       // 旧 redis.*（历史兼容）🧱
        "redis.clients",               // Jedis 客户端 🔴

        // =========================== 🔍 Search / Big Data / Streaming ===========================
        "co.elastic",                  // Elastic/X-Pack（商业/扩展）🧩
        "org.apache",                  // Apache 顶级兜底（Commons/Lucene/Kafka/Hadoop/Spark/Flink/POI…）🏴
        "org.elasticsearch",           // Elasticsearch 搜索 🔎
        "org.roaringbitmap",           // RoaringBitmap（位图索引/分析）🦔
        "org.tartarus",                // Lucene 词典/分词 📚

        // =========================== 🧩 OSGi / Containers / Servers ===========================
        "org.apache.felix",            // Apache Felix（OSGi）🧰
        "org.apache.tomcat",           // Tomcat 容器 🐱
        "org.eclipse",                 // Eclipse 顶级（含 jgit/jetty 等）🧩
        "org.eclipse.jetty",           // Jetty 服务器 ✈️
        "org.glassfish",               // Glassfish/Jersey/EL 实现 🐟
        "org.osgi",                    // OSGi API 🧩
        "org.wildfly",                 // WildFly 应用服务器 🐃
        "org.jboss",                   // JBoss/WildFly 生态 🐃

        // =========================== 🧾 Serialization / Formats / OpenAPI ===========================
        "com.ctc.wstx",                // Woodstox（StAX 实现）🌲
        "com.eclipsesource",           // minimal-json 等 🧼
        "com.fasterxml",               // Jackson 全家桶 🧰
        "freemarker",                  // FreeMarker 旧根包 🧾
        "org.glassfish.jersey",        // Jersey（JAX-RS 实现）🧴
        "org.json",                    // org.json.* JSON 实现 🧱
        "org.openxmlformats",          // OOXML 模式/格式 📄
        "org.yaml",                    // SnakeYAML（org.yaml.*）🐍

        // =========================== 🧪 Testing ===========================
        "examples",                    // 示例包（不应扫描）🎓
        "junit",                       // JUnit 4（历史前缀）🧪
        "org.assertj",                 // AssertJ 断言 ✅
        "org.hamcrest",                // Hamcrest 断言 🎯
        "org.junit",                   // JUnit（4/5 统一 org 前缀）🧪
        "org.mockito",                 // Mockito 模拟 🕵️
        "org.opentest4j",              // OpenTest4J（JUnit 平台）🧱
        "org.seleniumhq.selenium",     // Selenium 浏览器驱动 🧭

        // =========================== 🖼️ Templating / Office / HTML ===========================
        "com.lowagie",                 // iText 老包名（PDF 处理）📄
        "com.hubspot",                 // Mustache 模板引擎 🖼️
        "jinjava",                     // Jinjava 模板引擎 🍵
        "org.apache.pdfbox",           // PDFBox PDF 处理 📄
        "org.apache.poi",              // Apache POI（Excel/Word/PPT）📊
        "org.thymeleaf",               // Thymeleaf 模板引擎 🍃
        "org.jsoup",                   // Jsoup HTML 解析 🧽

        // =========================== 📬 MQ / Scheduling ===========================
        "com.rabbitmq",                // RabbitMQ 客户端 🐇

        // ===========================  微信 ===========================
        "org.dom4j",                   // dom4j XML 解析 📜
        "me.chanjar",                  // 微信相关依赖包 🧧
        "com.thoughtworks",            // XStream（微信 SDK 依赖）🚢
        "com.tencent",                 // 腾讯相关依赖包 🐯

        // =========================== 📜 Specs / Standards / Misc ===========================
        "com.apache",                  // 非标准组织包（偶见第三方误用，兜底）🧯
        "com.carrotsearch",            // HPPC 等性能工具 🥕
        "com.github",                  // 广义第三方（GitHub 组织发布的库）🐙
        "com.google",                  // Google 生态（Guava/Guice/Gson/Proto 等）🔎
        "com.googlecode",              // Google Code 旧组织名 🏚️
        "com.graphbuilder",            // GraphBuilder（历史库）🕸️
        "com.hp",                      // HP 相关组件 🖨️
        "com.intellij",                // IntelliJ 注解/工具 💡
        "com.jcraft",                  // JSch & jzlib 同组织（补全别名根）🔐
        "com.jcraft.jzlib",            // jzlib 压缩库（别名包）🧱
        "com.microfocus",              // Micro Focus 相关 📠
        "com.opencsv",                 // OpenCSV 表格解析 📑
        "com.sun",                     // com.sun.*（JDK 专有/内部）🌞
        "com.tdunning",                // t-digest 分位数算法 📈
        "com.terracottatech",          // Terracotta 技术 🧱
        "com.aliyuncs",                // 阿里云旧 SDK 别名包 ☁️
        "org.LatencyUtils",            // LatencyUtils 延迟工具 ⏱️
        "com.codahale",                // Metrics 库（旧根包）📏
        "ch.obermuhlner",              // fast-uuid 等工具 🆔
        "org.jspecify",                // JSpecify 注解规范 🏷️
        "edu.umd",                     // UMD 教育机构（Guava 旧包）🎓
        "org.immutables",              // Immutables 注解处理器 🏷️
        "connectjar",                  // 构建/打包工具生成命名空间 🧳
        "fastparse",                   // Scala FastParse ⚡
        "geny",                        // Scala geny 工具 🧪
        "io.github",                   // 广义 io.github.* 第三方 🧃
        "io.swagger",                  // Swagger/OpenAPI 工具链（冗余展示于 RPC 区，保留）📜
        "joptsimple",                  // 命令行解析 🧵
        "me.escofflier",               // Vert.x 相关示例/扩展 📝
        "mousio",                      // etcd4j（org.mousio）🧭
        "net",                         // 顶级 net.*（众多第三方）🌐
        "org.HdrHistogram",            // HdrHistogram 📊
        "org.apache.maven",            // Maven 模型/插件等 🧱
        "org.apiguardian",             // API Guardian 注解 🛡️
        "org.camunda",                 // Camunda/Spin/Connect（BPM）📋
        "org.cliffc",                  // HPPC 作者包 👤
        "org.eclipse",                 // Eclipse 顶级（冗余展示于 OSGi，保留）🧩
        "org.etsi",                    // ETSI 标准组织包 🛰️
        "org.glassfish",               // Glassfish（冗余展示于 OSGi，保留）🐟
        "org.intellij",                // IntelliJ 注解 💡
        "org.joda",                    // Joda-Time（历史库，类多）⏳
        "org.jcodings",                // JRuby 编码库（与 org.joni 搭配使用）🔤  ← 新增
        "org.joni",                    // JRuby 正则实现 🧬
        "org.jvnet",                   // Java.net 旧组织 🏷️
        "org.springframework",         // Spring 全家桶 🌱
        "org.w3",                      // W3 相关 🛰️
        "org.w3c",                     // W3C DOM/SAX 🌐
        "org.xml",                     // XML 工具/解析 🧩
        "org.xmlpull",                 // XMLPull 解析器 📜
        "picocli",                     // PicoCLI 命令行 📟
        "protostream",                 // Infinispan Protostream 🧱
        "scala",                       // Scala 标准库 🅂
        "sourcecode"                   // Scala sourcecode 宏 🔤
    };
}
