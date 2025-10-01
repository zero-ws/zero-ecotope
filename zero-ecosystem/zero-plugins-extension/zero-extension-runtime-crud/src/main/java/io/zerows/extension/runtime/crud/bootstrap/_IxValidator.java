package io.zerows.extension.runtime.crud.bootstrap;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.Rule;
import io.zerows.extension.runtime.crud.eon.IxFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

;

/*
 * Post validation in worker here
 * Because this module is dynamic inject rules for validation, here could not be
 * implemented with Zero @Codex, instead it should be implemented with extension.
 * But the configuration file formatFail is the same as default @Codex.
 */
class IxValidator {
    /*
     * uri -> field1 = List<Rule>
     *        field2 = List<Rule>
     */
    private static final ConcurrentMap<String, ConcurrentMap<String, List<Rule>>> RULE_MAP =
        new ConcurrentHashMap<>();

    static void init() {
        final List<String> files = Ut.ioFiles(IxFolder.VALIDATOR, VPath.SUFFIX.YML);
        files.forEach(file -> {
            /* 1. Validator file under classpath */
            final String path = IxFolder.VALIDATOR + file;
            /* 2. JsonArray process */
            final JsonObject rules = Ut.ioYaml(path);
            final ConcurrentMap<String, List<Rule>> ruleMap = new ConcurrentHashMap<>();
            rules.fieldNames().forEach(field -> {
                /* 3. Rule List */
                final JsonArray ruleArray = rules.getJsonArray(field);
                ruleMap.put(field, getRules(ruleArray));
            });
            /* 4. Append rules */
            final String key = file.replace(VString.DOT + VPath.SUFFIX.YML, VString.EMPTY);

            /* 4. Logger */
            LOG.Init.info(IxValidator.class, "--- file = {0}, key = {1}", path, key);
            RULE_MAP.put(key, ruleMap);
        });
        LOG.Init.info(IxValidator.class, "IxValidator Finished ! Size = {0}", RULE_MAP.size());
    }

    private static List<Rule> getRules(final JsonArray ruleArray) {
        final List<Rule> ruleList = new ArrayList<>();
        Ut.itJArray(ruleArray, (item, index) -> {
            final Rule rule = Rule.create(item);
            ruleList.add(rule);
        });
        return ruleList;
    }

    static ConcurrentMap<String, List<Rule>> getRules(final String actor) {
        ConcurrentMap<String, List<Rule>> rules = RULE_MAP.get(actor);
        if (null == rules) {
            rules = new ConcurrentHashMap<>();
        }
        return rules;
    }
}
