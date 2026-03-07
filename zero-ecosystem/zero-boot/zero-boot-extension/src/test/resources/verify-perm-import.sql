-- 验证权限导入结果

-- 1. 统计各表记录数
SELECT 'S_PERMISSION' as table_name, COUNT(*) as count FROM S_PERMISSION
UNION ALL
SELECT 'S_ACTION', COUNT(*) FROM S_ACTION
UNION ALL
SELECT 'S_RESOURCE', COUNT(*) FROM S_RESOURCE
UNION ALL
SELECT 'R_ROLE_PERM', COUNT(*) FROM R_ROLE_PERM;

-- 2. 查看 S_PERMISSION 数据
SELECT
    code,
    identifier,
    name,
    type,
    directory,
    comment
FROM S_PERMISSION
ORDER BY type, directory, name;

-- 3. 查看 S_ACTION 数据
SELECT
    code,
    name,
    method,
    uri,
    level,
    permission_id,
    resource_id
FROM S_ACTION
ORDER BY code;

-- 4. 查看 S_RESOURCE 数据
SELECT
    code,
    name,
    type,
    identifier,
    mode_role
FROM S_RESOURCE
ORDER BY code;

-- 5. 查看 R_ROLE_PERM 关联数据
SELECT
    rp.role_id,
    r.code as role_code,
    rp.perm_id,
    p.code as perm_code
FROM R_ROLE_PERM rp
LEFT JOIN S_ROLE r ON rp.role_id = r.id
LEFT JOIN S_PERMISSION p ON rp.perm_id = p.id
ORDER BY r.code, p.code;

-- 6. 验证 identifier 是否正确
-- 应该从 PERM.yml 中读取，而不是目录路径
SELECT
    code,
    identifier,
    CASE
        WHEN identifier LIKE '%.%.%' THEN 'WRONG: 使用了目录路径'
        ELSE 'OK: 使用了 PERM.yml 中的值'
    END as validation
FROM S_PERMISSION
ORDER BY code;

-- 7. 验证 S_RESOURCE 的 identifier
-- 应该与对应的 S_PERMISSION 的 identifier 一致
SELECT
    r.code as resource_code,
    r.identifier as resource_identifier,
    p.identifier as permission_identifier,
    CASE
        WHEN r.identifier = p.identifier THEN 'OK'
        ELSE 'ERROR: identifier 不匹配'
    END as validation
FROM S_RESOURCE r
LEFT JOIN S_ACTION a ON r.id = a.resource_id
LEFT JOIN S_PERMISSION p ON a.permission_id = p.id
ORDER BY r.code;
