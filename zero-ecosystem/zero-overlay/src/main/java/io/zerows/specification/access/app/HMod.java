package io.zerows.specification.access.app;

import io.zerows.support.base.UtBase;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.modeling.norm.HNs;

/**
 * 「模块」
 * 当前模块所属应用的详细信息，包括应用的基本信息，应用的部署信息，应用的版本信息等等，主要包含
 * <pre><code>
 *     1. 所属应用
 *     2. 名空间（默认为null）
 *     3. Bundle（OSGI环境才使用）
 * </code></pre>
 * 模块接口针对 {@link HApp} 会有特殊的处理逻辑，同时包含了设置和获取两种方法
 *
 * @author lang : 2023-05-21
 */
public interface HMod {

    /**
     * 所属命名空间
     *
     * @return {@link HNs.HMeta}
     */
    default HNs.HMeta namespace() {
        return null;
    }

    /**
     * 当前模块隶属于哪个 Bundle 发布
     *
     * @return {@link HBundle}
     */
    default HBundle bundle() {
        return null;
    }

    // -------------- 应用相关引用
    HApp app();

    HMod app(HApp appRef);

    // -------------- 基础模块信息

    /**
     * 当前模块的唯一标识，此标识的计算规则
     * <pre><code>
     *    1. app = null,  {@see * /name}
     *    2. app != null, {@see id/name}
     * </code></pre>
     *
     * @return 返回标识值
     */
    String id();

    /**
     * 当前存储的模块对应的名称，一般以 m 前缀，如
     * <pre><code>
     *     mHotel
     *     mSetting
     *     mSecurity
     * </code></pre>
     *
     * @return 模块名称
     */
    String name();

    default <T> T value(final String field) {
        return this.value(field, null);
    }

    <T> T value(String field, T defaultValue);

    /**
     * -----------------------------------------------------------------------------
     * <p>
     * 3. Zero模块化专用目录规范<br/>
     * Zero模块化专用目录规范，主要用于 Zero Extension 的模块化处理，Zero Extension可以作为两种模式存在：
     * <ol>
     *     <li>Zero Extension直接使用静态方式作为依赖接入应用中（通常是单机版）</li>
     *     <li>Zero Extension使用 `动态方式` 接入应用（基于OSGI规范），参考下边章节的 Bundle 专用目录规范</li>
     * </ol>
     *
     * 在上述Zero Extension第一种静态接入模式中：由于模块本身会作为依赖的应用，所以需实现全路径唯一，且不可重复，因此针对横向的管理会很差，
     * 于是才有OSGI模式下的标准出现，本章节为第一种静态方式的依赖。直接参考：<a href="https://www.vertx-cloud.cn/document/doc-web/index.html#_%E8%B5%84%E6%BA%90%E7%BB%93%E6%9E%84">4.3.2.资源结构</a>。
     * </p>
     *
     * Zero模块内的核心目录如（假设模块名称 `mod`）
     * <pre><code>
     *     - /action/{code}              （横向）安全，资源管理，code唯一
     *     - /authority/{code}           （横向）安全，权限管理面板，code唯一
     *     - /cab/cn/{uri}               （横向）前端配置，非微前端下的前端统一目录
     *     - /modulat/mod                （横向）模块配置，code唯一为模块对应的名称
     *     - /plugin/mod                 （模块内）模块基础配置
     *       /plugin/sql/mod             （模块内）模块SQL配置
     *     - /pojo/{identifier}          （横向）POJO映射配置，identifier为静态模型文件名
     *     - /workflow/{code}            （横向）工作流配置，code微工作流定义的键值
     * </code></pre>
     *
     * 书写过程给中，小写是目录，大写是值，所有路径本身依靠小写来区分，基本命名规范
     * <pre><code>
     *     1. 目录本名直接使用小写命名
     *     2. 目录节点值则直接使用大写命名
     *     3. 当前目录的全路径（绝对路径）采用 V 大写命名
     * </code></pre>
     * </p>
     *
     * @author lang
     */
    interface Configuration {
        String ACTION = "action";
        String AUTHORITY = "authority";

        String CAB = "cab";
        String MODULAT = "modulat";

        String PLUGIN = "plugin";

        String WORKFLOW = "workflow";

        interface workflow {
            // workflow/{code}
            static String of(final String code) {
                return UtBase.fromMessage(WORKFLOW + "/{}", code);
            }
        }

        interface plugin {
            String SQL = "sql";

            // plugin/{module}
            static String of(final String mod) {
                return UtBase.fromMessage(PLUGIN + "/{}", mod);
            }

            interface mod {
                // plugin/{module}/configuration.json
                static String configuration_json(final String mod) {
                    return UtBase.fromMessage(plugin.of(mod) + "/configuration.json");
                }

                /**
                 * <pre><code>
                 *     - /plugin/mod/configuration.json  （模块内）主配置
                 *     - /plugin/mod/oob                 （模块内）模块基础配置
                 *     - /plugin/mod/oob/cab             （安全）管理员角色数据
                 *     - /plugin/mod/oob/data            （数据）OOB基础数据
                 *     - /plugin/mod/oob/menu            （菜单）模块菜单配置
                 *     - /plugin/mod/oob/modulat         （模块）模块化专用配置
                 *     - /plugin/mod/oob/module/crud     （扩展）zero-crud 模块专用配置
                 *     - /plugin/mod/oob/module/ui       （扩展）zero-ui 模块专用配置
                 *     - /plugin/mod/oob/role/           （安全）管理员角色权限数据
                 *     - /plugin/mod/oob/initialize.json （文件）可使用 aj jmod 处理的初始化配置文件
                 *     - /plugin/mod/oob/initialize.yml  （文件）Jooq Dao专用配置
                 *     - /plugin/mod/oob/module.json     （文件）开启 module/ 目录中的扩展配置功能
                 * </code></pre>
                 */
                // plugin/{module}/oob/
                interface oob {
                    // plugin/{module}/oob/
                    static String of(final String mod) {
                        return UtBase.fromMessage(PLUGIN + "/{}/oob", mod);
                    }

                    // plugin/{module}/oob/initialize.json
                    static String initialize_json(final String mod) {
                        return UtBase.fromMessage(oob.of(mod) + "/initialize.json");
                    }

                    // plugin/{module}/oob/initialize.yml
                    static String initialize_yml(final String mod) {
                        return UtBase.fromMessage(oob.of(mod) + "/initialize.yml");
                    }

                    // plugin/{module}/oob/module.json
                    static String module_json(final String mod) {
                        return UtBase.fromMessage(oob.of(mod) + "/module.json");
                    }
                }
            }


            /**
             * <pre><code>
             *     - /plugin/sql/mod/                  （SQL）模块内表结构信息
             *     - /plugin/sql/mod.properties        Liquibase专用配置
             *     - /plugin/sql/mod.yml               Liquibase专用配置
             * </code></pre>
             */
            interface sql {

                // plugin/sql/{module}.properties
                static String liquibase_properties(final String mod) {
                    return UtBase.fromMessage(PLUGIN + "/{}/{}.properties", SQL, mod);
                }

                // plugin/sql/{module}.yml
                static String liquibase_yml(final String mod) {
                    return UtBase.fromMessage(PLUGIN + "/{}/{}.yml", SQL, mod);
                }

                interface mod {
                    // plugin/sql/{module}
                    static String of(final String mod) {
                        return UtBase.fromMessage(PLUGIN + "/{}/{}", SQL, mod);
                    }
                }
            }
        }

        interface modulat {
            // modulat/{module}
            static String of(final String mod) {
                return UtBase.fromMessage(MODULAT + "/{}", mod);
            }
        }

        interface cab {
            // cab/directory
            String DIRECTORY = "directory";         // 文档管理目录专用配置

            // cab/{language}/{uri}
            static String of(final String language, final String uri) {
                return UtBase.fromMessage(CAB + "/{}/{}", language, uri);
            }
        }

        interface authority {
            // authority/{code}
            static String of(final String code) {
                return UtBase.fromMessage(AUTHORITY + "/{}", code);
            }
        }

        interface action {
            // action/{code}
            default String of(final String code) {
                return UtBase.fromMessage(ACTION + "/{}", code);
            }
        }
    }
}
