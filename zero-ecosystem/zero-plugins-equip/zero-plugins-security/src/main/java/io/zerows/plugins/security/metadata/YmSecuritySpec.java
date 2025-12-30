package io.zerows.plugins.security.metadata;

/**
 * @author lang : 2025-12-30
 */
public interface YmSecuritySpec {
    String __ = "security";
    String wall = "wall";

    interface limit {
        String __ = "limit";
        String session = "session";
        String token = "token";
        String timeout = "timeout";
        String types = "types";
    }

    interface scope {
        String __ = "scope";
        String app = "app";
        String tenant = "tenant";
    }

    interface basic {
        String __ = "basic";

        interface options {
            String __ = "options";
            String realm = "realm";
        }
    }

    interface captcha {
        String __ = "captcha";

        interface code {
            String __ = "code";
            String type = "type";
            String length = "length";
        }

        interface font {
            String __ = "font";
            String name = "name";
            String weight = "weight";
            String size = "size";
        }

        interface options {
            String __ = "options";
            String type = "type";
            String expiredAt = "expiredAt";
            String width = "width";
            String height = "height";
            String textAlpha = "textAlpha";
        }
    }

    interface oauth2 {
        String __ = "oauth2";

        interface options {
            String __ = "options";
            String callback = "callback";
        }
    }

    interface htdigest {
        String __ = "htdigest";

        interface options {
            String __ = "options";
            String filename = "filename";
        }
    }

    interface jwt {
        String __ = "jwt";

        interface options {
            String __ = "options";
            String realm = "realm";

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
