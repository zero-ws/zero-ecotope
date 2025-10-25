package io.zerows.epoch.assembly;

/**
 * 说明：
 * 1) 本清单用于“前缀黑名单”（String.startsWith），命中则跳过扫描与反射；保持与你提供的条目完全一致，仅补充注释。
 * 2) 同时存在宽根包与子包不会增加扫描量（命中任一即可），但若过滤逻辑未基于类全名/包名调用 startsWith，可能会命不中从而导致扫描变多。
 *
 * @author lang : 2024-04-17
 */
interface ClassFilterPackage {

    String[] SKIP_PACKAGE = new String[]{
        "org.flywaydb",
        "apache",
        "android.annotation",          // Android 注解包（非服务器侧业务）
        "aj.org.objectweb",            // ASM 的阴影/重定位包（常见于 AspectJ）
        "camundajar",                  // Camunda shaded 包（Jar 内重定位）
        "camundafeel",                 // Camunda FEEL 表达式相关
        "cglib",                       // CGLIB 字节码生成
        "ch.qos.logback",              // Logback 日志实现
        "cn.hutool",                   // Hutool 工具集
        "co.elastic",                  // Elastic/X-Pack 等（商业/扩展命名）
        "co.paralleluniverse",         // Quasar 纤程库
        "connectjar",                  // 构建/打包工具生成的 connect JAR 命名空间
        "com.aliyun",                  // 阿里云 SDK
        "com.carrotsearch",            // HPPC 等性能工具
        "com.eclipsesource",           // minimal-json 等
        "com.esotericsoftware",        // Kryo / ReflectASM 等
        "com.fasterxml",               // Jackson 全家桶
        "com.github",                  // 广义第三方（GitHub 组织发布的库）
        "com.google",                  // Google 生态（Guava/Guice/Gson/Proto 等）
        "com.graphbuilder",            // GraphBuilder（历史库）
        "com.hazelcast",               // Hazelcast 集群/缓存
        "com.hp",                      // HP 相关组件
        "com.intellij",                // IntelliJ 注解/工具
        "com.jcraft.jzlib",            // jzlib 压缩库（别名包）
        "com.microfocus",              // Micro Focus 相关
        "com.lowagie",
        "com.microsoft",               // Microsoft/Azure/Graph 等 SDK
        "com.mysql",                   // MySQL JDBC 驱动
        "com.networknt",               // light-4j / JSON Schema Validator 等
        "com.netflix",                 // Netflix OSS（Feign/Hystrix/Archaius 等）
        "com.oracle",                  // Oracle 相关（JDBC/SDK）
        "com.opencsv",                 // OpenCSV
        "com.sun",                     // JDK 专有/内部（com.sun.*）
        "com.tdunning",                // t-digest 等
        "com.zaxxer",                  // HikariCP 连接池
        "com.codahale",                // Codahale Metrics（早期 Dropwizard）
        "de.javakaffee",               // Kryo-serializers 等
        "de.odysseus",                 // JUEL/JOOL 等
        "examples",                    // 示例代码包（不应扫描）
        "fastparse",                   // Scala FastParse
        "feign",                       // Netflix Feign（早期 groupId）
        "freemarker",                  // FreeMarker（旧根包）
        "geny",                        // Scala geny 工具
        "io.github",                   // 广义第三方（io.github.* 发布的库）
        "io.grpc",                     // gRPC 栈
        "io.micrometer",               // Micrometer 指标
        "io.netty",                    // Netty 网络框架
        "io.opentracing",              // OpenTracing（旧观测标准）
        "io.prometheus",               // Prometheus Java 客户端
        "io.perfmark",                 // PerfMark（gRPC 性能标记）
        "io.reactivex",                // RxJava（1.x 根包）
        "io.r2dbc",                    // R2DBC SPI/驱动
        "io.swagger",                  // Swagger/OpenAPI 工具链
        "io.termd",                    // termd 终端/TTY
        "io.vertx",                    // Vert.x 栈
        "jakarta",                     // Jakarta EE API（Servlet/JAX-RS/JPA/Validation …）
        "joptsimple",                  // 命令行解析
        "java",                        // JDK 标准库（java.*）
        "java.util.concurrent",        // J.U.C（冗余但保留：已被 java.* 覆盖）
        "javassist",                   // Javassist 字节码库
        "javax",                       // 旧 Javax API（向后兼容）
        "jdk",                         // JDK 内部模块
        "junit",                       // JUnit 4（冗余：下方有 org.junit）
        "kotlin",                      // Kotlin 标准库
        "lombok",                      // Lombok 编译期注解
        "liquibase",                   // Liquibase（历史根包名）
        "IMPL-JARS",                   // 自定义/实现 Jar 占位命名
        "me.escofflier",               // Vert.x 相关示例/扩展
        "mousio",                      // etcd4j（org.mousio）
        "net",                         // 顶级 net.*（许多第三方在此）
        "nonapi",                      // 非公开 API 命名空间（常见 shaded）
        "oracle",                      // Oracle（历史/别名包）
        "org.apiguardian",             // API Guardian 注解
        "org.HdrHistogram",            // HdrHistogram
        "org.aopalliance",             // AOP Alliance
        "org.aspectj",                 // AspectJ 相关
        "org.apache",                  // Apache 顶级（Commons/Lucene/Kafka/Hadoop/Spark/Flink/POI…）
        "org.bouncycastle",            // BouncyCastle 加密
        "org.camunda",                 // Camunda/Spin/Connect
        "org.checkerframework",        // Checker Framework 注解
        "org.cliffc",                  // HPPC 作者包
        "org.conscrypt",               // Conscrypt TLS 提供者
        "org.codehaus",                // Codehaus 旧组织（Groovy/Janino 等）
        "org.eclipse",                 // Eclipse 顶级（包括 Jetty/JGit 等）
        "org.elasticsearch",           // Elasticsearch 栈
        "org.etsi",                    // ETSI 标准组织包
        "org.glassfish",               // Glassfish/Jersey/EL 实现
        "org.hamcrest",                // Hamcrest 断言
        "org.hibernate",               // Hibernate ORM
        "org.intellij",                // IntelliJ 注解
        "org.infinispan",              // Infinispan 数据网格
        "org.joni",                    // JRuby 正则实现
        "org.jcodings",                // JRuby 编码库
        "org.jboss",                   // JBoss/WildFly 生态
        "org.jetbrains",               // JetBrains 注解
        "org.jgroups",                 // JGroups 集群
        "org.joda",                    // Joda-Time 等
        "org.jooq",                    // jOOQ
        "org.json",                    // org.json.* JSON 实现
        "org.jspecify",                // JSpecify 注解
        "org.junit",                   // JUnit（4/5 统一使用的 org 前缀）
        "org.jvnet",                   // Java.net 旧组织
        "org.mvel2",                   // MVEL 表达式语言
        "org.neo4j",                   // Neo4j（除 driver 外的组件）
        "org.objenesis",               // Objenesis（无构造器实例化）
        "org.objectweb",               // OW2/ASM 旧组织名总前缀
        "org.opentest4j",              // OpenTest4J（JUnit 平台依赖）
        "org.openxmlformats",          // OOXML 模式/格式
        "org.osgi",                    // OSGi API
        "org.reactivestreams",         // Reactive Streams 标准
        "org.reflections",             // Reflections 扫描库
        "org.slf4j",                   // SLF4J 日志门面
        "org.tartarus",                // Lucene 词典等（Tartarus）
        "org.w3c",                     // W3C DOM/SAX
        "org.w3",                      // W3 相关
        "org.wildfly",                 // WildFly 应用服务器
        "org.xml",                     // XML 工具/解析
        "org.yaml",                    // SnakeYAML（org.yaml.*）
        "protostream",                 // Infinispan Protostream
        "picocli",                     // PicoCLI 命令行
        "redis",                       // 旧 redis.* 根包（历史兼容，标准应为 redis.clients）
        "reactor",                     // Reactor 旧根（与 io.projectreactor 并存）
        "scala",                       // Scala 标准库
        "sourcecode",                  // Scala sourcecode 宏
        "schemaorg_apache_xmlbeans",   // XMLBeans 阴影包（OOXML 相关）
        "sun",                         // Sun/Oracle 专有实现
        "META-INF",                    // JAR 元数据/清单（重复出现，无功能影响）
        // OSGI Processing
        "org.jline",                   // JLine 命令行
        "org.easymock",                // EasyMock
        "org.fusesource",              // FuseSource（Jansi/LevelDB JNI 等）
    };
}
