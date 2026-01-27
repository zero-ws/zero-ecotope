-- liquibase formatted sql

-- changeset Lang:h-tag-entity-1
-- 关联表：R_TAG_OBJECT
DROP TABLE IF EXISTS R_TAG_OBJECT;
CREATE TABLE IF NOT EXISTS R_TAG_OBJECT
(
    `TAG_ID`
    VARCHAR
(
    36
) COMMENT '「tagId」- 标签ID',
    `ENTITY_TYPE` VARCHAR
(
    64
) COMMENT '「entityType」- 关联类型',
    `ENTITY_ID` VARCHAR
(
    36
) COMMENT '「entityId」- 关联实体ID',
    `LINK_COMPONENT` VARCHAR
(
    255
) COMMENT '「linkComponent」- 关联执行组件（扩展用）',
    `COMMENT` TEXT COMMENT '「comment」- 关系备注',
    PRIMARY KEY
(
    `TAG_ID`,
    `ENTITY_TYPE`,
    `ENTITY_ID`
)
    );