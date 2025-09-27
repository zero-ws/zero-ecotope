-- liquibase formatted sql

-- changeset Lang:d-refer-1
-- 关联表：D_REFER
/**
  此关联表主要用于描述文档引用表，引用表主要包括：
  -- 文档：Doc
  -- 章节：Segment
  -- 条款：Clause
  -- 考卷：Paper

  内置关联：
  1）拆分可记录章节到条款的关联关系
  2）章节到章节的引用，条款到条款的引用
  外置关联：
  两两关联
  且此关联是跨文档关联，当两个文档产生关联时才会使用此表，文档本身是一个树型结构

  ======
  关联关系所关联的实体必须是具有文档属性的实体，即上述提到的各种内容关联，而不可以是类似考试、答题卡这种特殊实体
 */
DROP TABLE IF EXISTS D_REFER;
CREATE TABLE IF NOT EXISTS D_REFER
(
    `FROM_ID`         VARCHAR(36) COMMENT '「fromId」- 从ID',
    `FROM_TYPE`       VARCHAR(64) COMMENT '「fromType」- 从类型',
    `TO_ID`           VARCHAR(36) COMMENT '「toId」- 到ID',
    `TO_TYPE`         VARCHAR(64) COMMENT '「toType」- 到类型',
    /*
     inline = true
     表示内联关系，文档内部：章节到条款的关联关系
     inline = false
     表示外联关系，文档三层和外联文档三层之间的两两对应关系
     */
    `INLINE`          BIT COMMENT '「inline」- 是否内联',
    `COMMENT`         TEXT COMMENT '「comment」- 关系备注',
    `REFER_COMPONENT` VARCHAR(255) COMMENT '「referComponent」- 关联执行组件（扩展用）',
    PRIMARY KEY (`FROM_ID`, `FROM_TYPE`, `TO_ID`, `TO_TYPE`)
);