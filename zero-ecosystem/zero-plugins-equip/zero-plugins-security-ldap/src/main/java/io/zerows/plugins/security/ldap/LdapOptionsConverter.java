package io.zerows.plugins.security.ldap;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converter for {@link LdapOptions}.
 * Handles mapping between JSON/YAML keys and Java fields.
 */
@SuppressWarnings("unchecked")
public class LdapOptionsConverter {

    static void fromJson(final Iterable<java.util.Map.Entry<String, Object>> json, final LdapOptions obj) {
        for (final java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
                case "url":
                    if (member.getValue() instanceof String) {
                        obj.setUrl((String) member.getValue());
                    }
                    break;
                case "username":
                    if (member.getValue() instanceof String) {
                        obj.setUsername((String) member.getValue());
                    }
                    break;
                case "password":
                    if (member.getValue() instanceof String) {
                        obj.setPassword((String) member.getValue());
                    }
                    break;
                case "base":
                    if (member.getValue() instanceof String) {
                        obj.setBase((String) member.getValue());
                    }
                    break;
                case "referral":
                    if (member.getValue() instanceof String) {
                        obj.setReferral((String) member.getValue());
                    }
                    break;
                // 兼容原生配置名 (authenticationMechanism) 和简写 (mechanism)
                case "authenticationMechanism":
                case "mechanism":
                    if (member.getValue() instanceof String) {
                        obj.setMechanism((String) member.getValue());
                    }
                    break;
                // 仅支持标准配置格式 (user-email)
                case "user-email":
                    if (member.getValue() instanceof String) {
                        obj.setUserEmail((String) member.getValue());
                    }
                    break;
                // 仅支持标准配置格式 (user-id)
                case "user-id":
                    if (member.getValue() instanceof String) {
                        obj.setUserId((String) member.getValue());
                    }
                    break;
                // 兼容原生配置名 (authenticationQuery) 和自定义名 (user-query)
                case "authenticationQuery":
                case "user-query":
                    if (member.getValue() instanceof JsonArray) {
                        final List<String> list = new ArrayList<>();
                        ((Iterable<Object>) member.getValue()).forEach(item -> {
                            if (item instanceof String) {
                                list.add((String) item);
                            }
                        });
                        obj.setUserQuery(list);
                    } else if (member.getValue() instanceof String) {
                        obj.addUserQuery((String) member.getValue());
                    }
                    break;
            }
        }
    }

    static void toJson(final LdapOptions obj, final JsonObject json) {
        toJson(obj, json.getMap());
    }

    static void toJson(final LdapOptions obj, final Map<String, Object> json) {
        if (obj.getUrl() != null) {
            json.put("url", obj.getUrl());
        }
        if (obj.getUsername() != null) {
            json.put("username", obj.getUsername());
        }
        if (obj.getPassword() != null) {
            json.put("password", obj.getPassword());
        }
        if (obj.getBase() != null) {
            json.put("base", obj.getBase());
        }
        if (obj.getReferral() != null) {
            json.put("referral", obj.getReferral());
        }
        // 保持自身对象风格，使用 mechanism 简写
        if (obj.getMechanism() != null) {
            json.put("mechanism", obj.getMechanism());
        }
        if (obj.getUserEmail() != null) {
            json.put("user-email", obj.getUserEmail());
        }
        if (obj.getUserId() != null) {
            json.put("user-id", obj.getUserId());
        }
        if (obj.getUserQuery() != null && !obj.getUserQuery().isEmpty()) {
            final JsonArray array = new JsonArray();
            obj.getUserQuery().forEach(array::add);
            json.put("user-query", array);
        }
    }

    /**
     * 🟢 提取 Vert.x 原生配置 (Native Options)
     * 仅包含: url, referral, authenticationMechanism, authenticationQuery
     */
    static JsonObject toNativeOption(final LdapOptions obj, final String query) {
        final JsonObject json = new JsonObject();

        if (obj.getUrl() != null) {
            json.put("url", obj.getUrl());
        }
        // Native 必须使用 authenticationMechanism
        if (obj.getMechanism() != null) {
            json.put("authenticationMechanism", obj.getMechanism());
        }
        if (obj.getReferral() != null) {
            json.put("referral", obj.getReferral());
        }
        // Native 必须使用 authenticationQuery (单条)
        if (query != null) {
            json.put("authenticationQuery", query);
        }

        return json;
    }
}