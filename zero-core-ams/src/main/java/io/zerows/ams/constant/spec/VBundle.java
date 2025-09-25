package io.zerows.ams.constant.spec;

import io.zerows.ams.util.HUt;

/**
 * -----------------------------------------------------------------------------
 * <p>
 * 4. Bundle接入目录规范<br/>
 *
 * Bundle模块基础规范，参考如下：<br/>
 *
 * Eclipse常用插件基础
 * <pre><code>
 * - /META-INF/MANIFEST.MF              模块描述文件
 * - plugin.xml                         插件配置文件（XML）
 * - plugin.properties                  插件配置文件（PROP）
 * - lib/                               依赖库
 * - schema/                            XSD专用文件存储目录
 * - xxx.jar                            JAR文件，主 Bundle
 * </code></pre>
 *
 * 自定义插件基础
 * <pre><code>
 * - lib/
 *   - extension/                       （扩展）第三方开发的扩展JAR文件
 * - modeler/                           （后端）模型库
 *   - emf/                                  - EMF模型库
 *   - atom/                                 - zero-atom 专用模型库
 *     - meta/                                  - 定义
 *     - stored/                             - 引用
 *     - rule/                                  - 规则
 * - init/                              （后端）注册初始化目录，元数据出厂设置
 *   - modeler/                              - 模型注册器
 *   - get/                                - 存储注册器
 *     - ddl/                                   - SQL存储中的基础文件
 *   - cloud/                                - 云端连接器
 *   - development/                          - 开发层编辑器
 *   - oob/                                  - 初始化元数据
 *     - resource/                              - 专用资源文件
 * - backend/                           （后端）后端 Web 主目录
 *   - scripts/                         （后端）后端 Web 专用主脚本
 *     - {type}/                             - 不同类型的脚本，后期扩展脚本引擎
 *   - endpoint/                        （后端）后端 Web 端地址
 *     - api/                                - Web服务接口，RESTful
 *     - web-socket/                         - Web Socket服务
 *     - service-bus/                        - Service Bus服务总线
 *   - webapp/                          （后端）JSP等类型内容
 *     - WEB-INF/                            - 传统资源文件，包括 web.xml / Servlet
 *   - components/                      （后端）组件配置目录
 *     - task/                               - 任务
 *     - handler/                            - 处理器
 *     - event/                              - 事件
 *     - validator/                          - 验证器
 * - frontend/                          （前端）前端配置主目录
 *   - assembly/                        （前端）Native前端处理
 *   - cab/                             （前端）资源包
 *     - {language}/                         - 语言目录（多语言环境）
 *   - scripts/                         （前端）前端脚本（Ts、Js）
 *   - skin/                            （前端）前端皮肤处理专用
 *   - images/                          （前端）图片相关资源
 *     - icon/                               - 图标资源
 *   - components/                      （前端）自定义组件（扩展）
 * </code></pre>
 * </p>
 *
 * @author lang : 2023-05-28
 */
public interface VBundle {
    String META_INF = "META-INF";
    String PLUGIN_XML = "plugin.xml";
    String PLUGIN_PROPERTIES = "plugin.properties";
    String LIB = "lib";
    String INIT = "init";
    String MODELER = "modeler";
    String SCHEMA = "schema";
    String BACKEND = "backend";
    String FRONTEND = "frontend";

    /**
     * 计算 Bundle 名称，目录模式，如果是目录模式，则内容遵循
     * {@link VBundle} 内置目录规范
     *
     * @param group    组
     * @param artifact ID
     * @param version  版本
     *
     * @return Bundle 名称
     */
    static String name(final String group, final String artifact, final String version) {
        return HUt.fromMessage("{}.{}_{}", group, artifact, version);
    }

    /**
     * 计算 Bundle 名称，文件模式，如果是文件模式，jar 文件内部资源
     * 路径遵循 {@link VBundle} 内置目录规范
     *
     * @param group    组
     * @param artifact ID
     * @param version  版本
     *
     * @return Bundle 名称.jar
     */
    static String name_jar(final String group, final String artifact, final String version) {
        return HUt.fromMessage("{}.{}_{}.jar", group, artifact, version);
    }

    interface frontend {
        // frontend/assembly
        String ASSEMBLY = FRONTEND + "/assembly";
        // frontend/cab
        String CAB = FRONTEND + "/cab";
        // frontend/scripts
        String SCRIPTS = FRONTEND + "/scripts";
        // frontend/skin
        String SKIN = FRONTEND + "/skin";

        // frontend/images
        String IMAGES = FRONTEND + "/images";

        // frontend/components
        String COMPONENTS = FRONTEND + "/components";

        interface components {

        }

        interface images {
            // frontend/images/icon
            String ICON = IMAGES + "/icon";
        }

        interface skin {
            // frontend/skin/{name}
            static String of(final String name) {
                return HUt.fromMessage(SKIN + "/{}", name);
            }
        }

        interface scripts {
            // frontend/scripts/{type}
            static String of(final String type) {
                return HUt.fromMessage(SCRIPTS + "/{}", type);
            }
        }

        interface cab {
            // frontend/cab/{language}
            static String of(final String language) {
                return HUt.fromMessage(CAB + "/{}", language);
            }
        }
    }

    interface backend {
        // backend/scripts
        String SCRIPTS = BACKEND + "/scripts";
        // backend/endpoint
        String ENDPOINT = BACKEND + "/endpoint";
        // backend/webapp
        String WEBAPP = BACKEND + "/webapp";
        // backend/components
        String COMPONENTS = BACKEND + "/components";

        interface components {
            // backend/components/task
            String TASK = COMPONENTS + "/task";
            // backend/components/handler
            String HANDLER = COMPONENTS + "/handler";
            // backend/components/event
            String EVENT = COMPONENTS + "/event";
            // backend/components/validator
            String VALIDATOR = COMPONENTS + "/validator";
            // backend/components/integration
            String INTEGRATION = COMPONENTS + "/integration";
        }

        interface webapp {
            String WEB_INF = WEBAPP + "/WEB-INF";
        }

        interface endpoint {
            // backend/endpoint/api
            String API = ENDPOINT + "/api";
            // backend/endpoint/ipc
            String IPC = ENDPOINT + "/ipc";
            // backend/endpoint/rpc
            String RPC = ENDPOINT + "/rpc";
            // backend/endpoint/web-socket
            String WEB_SOCKET = ENDPOINT + "/web-socket";
            // backend/endpoint/service-bus
            String SERVICE_BUS = ENDPOINT + "/service-bus";
        }

        interface scripts {
            // backend/scripts/{type}
            static String of(final String type) {
                return HUt.fromMessage(SCRIPTS + "/{}", type);
            }
        }
    }

    interface init {
        // init/modeler
        String MODELER = INIT + "/modeler";
        // init/get
        String STORE = INIT + "/get";
        // init/cloud
        String CLOUD = INIT + "/cloud";
        // init/development
        String DEVELOPMENT = INIT + "/development";

        String OOB = INIT + "/oob";

        interface oob {
            String RESOURCE = OOB + "/resource";
        }

        interface store {
            // init/get/ddl
            String DDL = STORE + "/ddl";
        }
    }

    interface modeler {
        String EMF = MODELER + "/emf";

        String ATOM = MODELER + "/argument";

        interface atom {
            String META = ATOM + "/meta";
            String REFERENCE = ATOM + "/stored"; // 不绑定，无子配置
            String RULE = ATOM + "/rule";   // 不绑定，无子配置

            static String meta_json(final String identifier) {
                return HUt.fromMessage(META + "/{}.json", identifier);
            }
        }
    }

    interface schema {
        // schema/xsd
        String XSD = SCHEMA + "/xsd";
        // schema/dtd
        String DTD = SCHEMA + "/dtd";
    }

    interface lib {
        // lib/extension
        String EXTENSION = LIB + "/extension";
    }

    interface meta_inf {
        // META-INF/MANIFEST.MF
        String MANIFEST_MF = META_INF + "/MANIFEST.MF";
        String NATIVE = META_INF + "native";
        String SERVICES = META_INF + "services";
    }
}
