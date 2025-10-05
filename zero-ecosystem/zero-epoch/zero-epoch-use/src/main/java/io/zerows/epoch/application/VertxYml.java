package io.zerows.epoch.application;

public interface VertxYml {
    
    interface boot {
        String __ = "boot";

        interface pre {
            String __ = "pre";
            String component = "component";
            String config = "config";
        }

        interface on {
            String __ = "on";
            String component = "component";
            String config = "config";
        }

        interface off {
            String __ = "off";
            String component = "component";
            String config = "config";
        }
    }

    interface server {
        String __ = "server";
        String port = "port";
        String address = "address";

        interface options {
            String __ = "options";
            String ssl = "ssl";
            String useAlpn = "useAlpn";
            String clientAuth = "clientAuth";
            String idleTimeout = "idleTimeout";
            String compressionSupported = "compressionSupported";
            String maxWebSocketFrameSize = "maxWebSocketFrameSize";
            String maxWebSocketMessageSize = "maxWebSocketMessageSize";

            interface keyStoreOptions {
                String __ = "keyStoreOptions";
                String type = "type";
                String path = "path";
                String password = "password";
            }

            interface trustStoreOptions {
                String __ = "trustStoreOptions";
                String type = "type";
                String path = "path";
                String password = "password";
            }
        }

        interface websocket {
            String __ = "websocket";
            String component = "component";

            interface config {
                String __ = "config";

                interface stomp {
                    String __ = "stomp";
                    String port = "port";
                    String secured = "secured";
                    String websocketBridge = "websocketBridge";
                    String websocketPath = "websocketPath";
                }
            }
        }
    }

    interface vertx {
        String __ = "vertx";

        interface cloud {
            String __ = "cloud";
            String server_addr = "server-addr";
            String username = "username";
            String password = "password";
            String name = "name";

            interface nacos {
                String __ = "nacos";
                String server_addr = "server-addr";
                String username = "username";
                String password = "password";
                String name = "name";

                interface discovery {
                    String __ = "discovery";
                    String server_addr = "server-addr";
                    String namespace = "namespace";
                }

                interface config {
                    String __ = "config";
                    String server_addr = "server-addr";
                    String namespace = "namespace";
                    String prefix = "prefix";
                    String file_extension = "file-extension";
                }
            }
        }

        interface config {
            String __ = "config";
            String import_ = "import";

            interface instance {
                String __ = "instance";
                String name = "name";

                interface options {
                    String __ = "options";
                    String maxEventLoopExecuteTime = "maxEventLoopExecuteTime";
                    String maxWorkerExecuteTime = "maxWorkerExecuteTime";
                    String preferNativeTransport = "preferNativeTransport";
                    String blockedThreadCheckInterval = "blockedThreadCheckInterval";
                    String eventLoopPoolSize = "eventLoopPoolSize";
                    String workerPoolSize = "workerPoolSize";
                    String internalBlockingPoolSize = "internalBlockingPoolSize";
                }
            }

            interface delivery {
                String __ = "delivery";
                String timeout = "timeout";
            }

            interface deployment {
                String __ = "deployment";

                interface instances {
                    String __ = "instances";
                    String worker = "worker";
                    String agent = "agent";
                }
            }
        }

        interface application {
            String __ = "application";
            String name = "name";

            interface cors {
                String __ = "cors";
                String origin = "origin";
            }
        }

        interface cluster {
            String __ = "cluster";
            String manager = "manager";

            interface options {
                String __ = "options";
                String clusterPublicHost = "clusterPublicHost";
                String clusterPublicPort = "clusterPublicPort";
            }
        }

        interface security {
            String __ = "security";
            String wall = "wall";

            interface jwt {
                String __ = "jwt";

                interface options {
                    String __ = "options";

                    interface jwtOptions {
                        String __ = "jwtOptions";
                        String algorithm = "algorithm";
                    }

                    interface keyStore {
                        String __ = "keyStore";
                        String type = "type";
                        String path = "path";
                        String password = "password";
                    }
                }
            }
        }

        interface datasource {
            String __ = "datasource";
            String dynamic = "dynamic";
            String url = "url";
            String username = "username";
            String password = "password";
            String driver_class_name = "driver-class-name";

            interface hikari {
                String __ = "hikari";
                String minimum_idle = "minimum-idle";
                String maximum_pool_size = "maximum-pool-size";
                String connection_timeout = "connection-timeout";
                String idle_timeout = "idle-timeout";
                String max_lifetime = "max-lifetime";
                String validation_timeout = "validation-timeout";
                String connection_test_query = "connection-test-query";
                String pool_name = "pool-name";
            }

            interface dynamic {
                String __ = "dynamic";
                String primary = "primary";
                String strict = "strict";

                interface _datasource {
                    String url = "url";
                    String username = "username";
                    String password = "password";
                    String instance = "instance";
                    String driver_class_name = "driver-class-name";
                }
            }
        }

        interface data {
            String __ = "data";

            interface redis {
                String __ = "redis";
                String host = "host";
                String port = "port";
                String password = "password";
                String database = "database";
                String timeout = "timeout";
            }
        }
    }

    interface dubbo {
        String __ = "dubbo";

        interface application {
            String __ = "application";
            String name = "name";
            String qosPort = "qosPort";
            String serialize_check_status = "serialize-check-status";
        }

        interface registry {
            String __ = "registry";
            String address = "address";

            interface parameters {
                String __ = "parameters";
                String namespace = "namespace";
                String username = "username";
                String password = "password";
            }
        }

        interface protocol {
            String __ = "protocol";
            String name = "name";
            String port = "port";
        }

        interface provider {
            String __ = "provider";
            String serialization_security_check = "serialization-security-check";
        }

        interface consumer {
            String __ = "consumer";
            String serialization_security_check = "serialization-security-check";
        }
    }

    interface app {
        String __ = "app";
        String id = "id";
        String tenant = "tenant";

        interface data {
            String __ = "data";
            String copyright = "copyright";
        }

        interface config {
            String __ = "config";
            String demo = "demo";
        }
    }

    interface storage {
        String __ = "storage";
    }

    interface excel {
        String __ = "excel";
        String pen = "pen";
        String temp = "temp";
        String tenant = "tenant";
    }

    interface flyway {
        String __ = "flyway";
        String locations = "locations";
        String schemas = "schemas";
        String table = "table";
        String baseline_on_migrate = "baseline-on-migrate";
        String clean_on_validation_error = "clean-on-validation-error";
        String enabled = "enabled";
        String encoding = "encoding";
        String group = "group";
        String out_of_order = "out-of-order";
        String skip_default_callbacks = "skip-default-callbacks";
        String skip_default_resolvers = "skip-default-resolvers";
        String sql_migration_prefix = "sql-migration-prefix";
        String sql_migration_separator = "sql-migration-separator";
        String sql_migration_suffixes = "sql-migration-suffixes";
        String validate_on_migrate = "validate-on-migrate";
        String placeholders = "placeholders";
        String placeholder_prefix = "placeholder-prefix";
        String placeholder_suffix = "placeholder-suffix";
        String resolvers = "resolvers";
        String callbacks = "callbacks";
        String target = "target";
        String url = "url";
        String user = "user";
        String password = "password";
        String driver_class_name = "driver-class-name";
        String connect_retries = "connect-retries";
        String init_sql = "init-sql";
        String mixed = "mixed";
        String ignore_future_migrations = "ignore-future-migrations";
        String ignore_missing_migrations = "ignore-missing-migrations";
        String installed_by = "installed-by";
    }

    interface sms {
        String __ = "sms";

        interface aliyun {
            String __ = "aliyun";
            String domain = "domain";
            String region_id = "region-id";
            String access_id = "access-id";
            String access_secret = "access-secret";
            String sign_name = "sign-name";
            String tpl_name = "tpl-name";
        }
    }

    interface plugins {
        String __ = "plugins";
    }
}