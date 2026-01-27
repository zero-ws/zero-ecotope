package io.zerows.epoch.spec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-05
 */
@Data
public class YmApplication implements Serializable {
    private String name;
    private YmElasticSearch elasticsearch;
    private YmNeo4j neo4j;
}
