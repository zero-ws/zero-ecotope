package io.zerows.extension.module.ambient.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RbacResourceUploadSessionTest {

    private static final String BASE =
        "plugins/zero-exmodule-ambient/security/RBAC_RESOURCE/应用管理/应用空间/文档管理/";

    @Test
    void shouldContainSessionUploadResourceDefinitions() {
        assertResourceExists("初始化上传会话@POST@_api_file_upload_session.yml");
        assertResourceExists("上传会话状态@GET@_api_file_upload_session_$token.yml");
        assertResourceExists("上传分片@POST@_api_file_upload_session_$token_chunk.yml");
        assertResourceExists("完成上传会话@POST@_api_file_upload_session_$token_complete.yml");
        assertResourceExists("取消上传会话@DELETE@_api_file_upload_session_$token.yml");
    }

    private static void assertResourceExists(final String filename) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Assertions.assertNotNull(
            classLoader.getResource(BASE + filename),
            () -> "missing RBAC resource: " + BASE + filename
        );
    }
}
