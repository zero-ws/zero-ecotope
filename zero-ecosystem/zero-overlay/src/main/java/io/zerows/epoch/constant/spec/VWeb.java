package io.zerows.epoch.constant.spec;

import io.zerows.epoch.constant.VString;
import io.zerows.epoch.enums.Environment;

import java.util.Objects;

/**
 * Bundle基础规范，当前版本为草稿，考虑如下几点：
 * -----------------------------------------------------------------------------
 * <p>
 * 1. 核心目录<br/>
 * 描述了标准目录空间下的基础内容，如：
 * <pre><code>
 *     全容器：
 *     - configuration/             系统配置目录
 *     - init/oob/                  出厂设置（初始化目录）
 *     - running/                   运行时目录
 *     - plugin/                    Zero Extension扩展目录
 *     容器和模块对接（OSGI）：
 *     - plugins/
 *     - features/
 *     - extensions/
 * </code></pre>
 * -----------------------------------------------------------------------------
 * <p>
 * 2. 全容器目录
 * <pre><code>
 *   - /configuration/library/      「库信息」
 *       - /system/                      系统库基础目录
 *       - /environment/                    内置应用共享库
 *       - /external/                    扩展库基础目录
 *   - /configuration/editor/       「编辑器」
 *       - /environment/                    默认带的产品内置编辑器
 *          - /{editor_1}/                  编辑器1
 *          - /{editor_2}/                  编辑器2
 *          - ...
 *       - /external/                    扩展编辑器目录
 *          - /{editor_1}/                  编辑器1
 *          - /{editor_2}/                  编辑器2
 *          - ...
 *   - /init/oob/                   「数据初始化」OOB数据初始化（元数据导入）
 *       - /secure/                      安全相关配置数据
 *       - /environment/                 环境相关配置数据
 *       - /navigation/                  服务于前端，由后端提供前端的导航基础（菜单部分的整体运行）
 *   - /running/                    「运行时」
 *       - /cache/                       运行时缓存基础
 *       - /log/                         运行时日志信息
 *
 *   - /plugin/                     「旧版插件」区别于OSGI插件，此目录现阶段主要用于 Zero Extension 的遗留系统，且此规范主要应用于
 *                                       单点系统部分，当您的应用是一个单机应用时，则需遵循此种规范，由于和 OSGI 插件规范近似，所以
 *                                       此处需区分 `plugins` 和 `plugin` 两个目录，不同目录代表的含义有所区别，新版中的 OSGI模块
 *                                       化处理完成后，此处的 `plugin` 目录将会被移除，统一使用 OSGI 插件规范。
 *       - /{extension_1}/               扩展1
 *       - /{extension_2}/               扩展2
 *       - ...
 *
 *   {OSGI}                          「OSGI容器」 Bundle外层（接入，遵循OSGI基础规范）
 *   - /features/                        功能
 *   - /plugins/                         插件
 *   - /extension/                   「Bundle扩展」（元模型级）
 *       - /{bundle_1}/                  此处扩展为系统级扩展，在注册流程中，如果有需要直接将Bundle针对系统的基础扩展放置到此处，
 *       - /{bundle_2}/                  并且根据Bundle本身的特性（identifier）构造对应的扩展目录，扩展出来的目录遵循基本的定义
 *                                       配置，您可以为新的Bundle提前执行环境预处理，包括：
 *                                       - 环境基础检查（准入规则）
 *                                       - 环境运行检查（环境扫描）
 *                                       - 运行状态检查（是否可激活、激活完成之后是否状态正常）
 * </code></pre>
 * </p>
 */
public interface VWeb {
    String CONFIGURATION = "configuration";
    String INIT = "init";
    String RUNTIME = "running";
    String PLUGIN = "plugin";
    // OSGI：OSGI部分全部采用复数形式
    String PLUGINS = "plugins";
    String FEATURES = "features";
    String EXTENSIONS = "extensions";

    String ATOM = "argument";

    interface atom {

        String TARGET = ATOM + "/target";

        static String of(final String name) {
            Objects.requireNonNull(name);
            return ATOM + "/" + name;
        }
    }

    interface runtime {
        // running/cache
        String CACHE = RUNTIME + "/cache";
        // running/log
        String LOG = RUNTIME + "/log";
        // running/configuration.json
        String CONFIGURATION_JSON = RUNTIME + "/configuration.json";
        // running/external/
        String EXTERNAL = RUNTIME + "/external";
        // running/environment/
        String ENVIRONMENT = RUNTIME + "/environment";

        interface external {
            // running/external/integration.json
            String INTEGRATION_JSON = EXTERNAL + "/integration.json";
        }

        interface environment {

            // running/environment/{0}-integration/
            static String ofIntegration(final Environment environment) {
                final String environmentName = environment.name().toLowerCase();
                return ENVIRONMENT + "/" + environmentName + "-integration/";
            }

            // running/environment/{0}-database.json
            static String ofDatabase(final Environment environment) {
                final String environmentName = environment.name().toLowerCase();
                return ENVIRONMENT + "/" + environmentName + "-database.json";
            }
        }

        // running/configuration.json
        interface configuration {
            String STELLAR = "stellar";                  /* Definition: stellar */
            String OPTIONS = "options";                  /* Definition: configuration options */
        }
    }

    interface init {
        // init/oob
        String OOB = INIT + "/oob";
        // init/permission
        String PERMISSION = INIT + "/permission";

        interface permission {
            // init/permission/ui.menu
            String UI_MENU = PERMISSION + "/ui.menu";

            interface ui_menu {
                // init/permission/ui.menu/role
                String ROLE = UI_MENU + "/role";

                // {root}/init/permission/ui.menu/{role}.json
                static String of(final String root, final String role) {
                    return root + UI_MENU + VString.SLASH + role + ".json";
                }

                interface role {
                    // init/permission/ui.menu/role/{role}.json
                    static String of(final String role) {
                        return ROLE + VString.SLASH + role + ".json";
                    }
                }
            }
        }

        interface oob {
            // init/oob/secure
            String SECURE = OOB + "/secure";
            // init/oob/navigation
            String NAVIGATION = OOB + "/navigation";
            /**
             * 前端对接目录：init/oob/cab
             * Cab目录主要用于前端UI连接，对应到表`UI_PAGE`和`UI_LAYOUT`，目录结构如下：
             * <pre><code>
             *     - init/oob/cab/component
             *       Page的UI配置
             *     - init/oob/cab/stored
             *       Layout（Container）容器的配置
             * </code></pre>
             */
            String CAB = OOB + "/cab";
            /**
             * 数据目录：init/oob/data
             * 此目录用于将基础数据导入到数据库中
             * <pre><code>
             *     - 1）服务目录数据：Service Catalog
             *     - 2）字典数据：Directory Data
             *     - 3）位置数据，对接进销存PSI：Location of WH
             *     - 4）系统菜单数据：System Menu / Extension Menu
             *     - 5）ACL资源树数据：Resource Tree Data for ACL
             * </code></pre>
             */
            String DATA = OOB + "/data";
            /**
             * 环境目录：init/oob/environment
             * 此目录用于环境数据的导入
             * <pre><code>
             *     - 1）默认的账号数据
             *     - 2）组织架构数据：Company / Dept / Team
             *     - 3）员工和档案数据：Employee / Identity
             * </code></pre>
             */
            String ENVIRONMENT = OOB + "/environment";
            /**
             * 集成环境目录：init/oob/integration
             * 此目录在最新的（ 0.9 < ）中使用，存储了和集成相关的数据信息
             * <pre><code>
             *     - 1）FTP
             *     - 2）RESTful
             *     - 3）SMS
             *     - 4）Email
             * </code></pre>
             */
            String INTEGRATION = OOB + "/integration";
            /**
             * 模块化专用目录：init/oob/modulat
             * 模块化目录会关联到模块部分 {@link VExtension} 的完整目录结构，并带上模块名称
             */
            String MODULAT = OOB + "/modulat";
            /**
             * 角色目录：init/oob/role
             * 角色专用数据，包含了权限相关信息
             * <pre><code>
             *     - 1）针对每个角色的权限设置脚本
             *     - 2）超级角色 LANG.YU 中存储的内容
             * </code></pre>
             * 直接运行 ./run-perm.sh 脚本可处理
             */
            String ROLE = OOB + "/role";

            /**
             * 工作流专用目录：init/oob/workflow，此目录关联到新开发的 `X_ACTIVITY_RULE` 功能，在新的工作流引擎中，您可以
             * 针对不同的工作流节点设置不同的活动执行规则，这些规则会被存储在此目录下
             * <pre><code>
             *     - 1）为当前操作生成最新的操作日志记录
             *     - 2）绑定一个 Hooker 的回调执行函数，可以在每个规则触发滞后执行
             *     - 3）和操作有关的提醒（Email/SMS）
             *     - 4）AOP 在流程行为中注入
             * </code></pre>
             * 条件检查使用 JEXL 部分
             */
            String WORKFLOW = OOB + "/workflow";

            interface workflow {
                static String of(final String name) {
                    Objects.requireNonNull(name);
                    return WORKFLOW + "/" + name;
                }
            }

            interface modulat {
                static String of(final String name) {
                    Objects.requireNonNull(name);
                    return MODULAT + "/" + name;
                }
            }

            interface role {
                static String of(final String role) {
                    Objects.requireNonNull(role);
                    return ROLE + "/" + role;
                }
            }

            interface cab {
                String COMPONENT = CAB + "/component";
                String CONTAINER = CAB + "stored";
            }
        }
    }

    interface configuration {
        // configuration/library
        String LIBRARY = CONFIGURATION + "/library";
        // configuration/editor
        String EDITOR = CONFIGURATION + "/editor";

        interface library {
            // configuration/library/system
            String SYSTEM = LIBRARY + "/system";
            // configuration/library/environment
            String INTERNAL = LIBRARY + "/environment";
            // configuration/library/external
            String EXTERNAL = LIBRARY + "/external";
        }

        interface editor {
            // configuration/editor/environment
            String INTERNAL = EDITOR + "/environment";
            // configuration/editor/external
            String EXTERNAL = EDITOR + "/external";
        }
    }
}
