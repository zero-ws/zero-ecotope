package io.zerows.extension.module.rbac.metadata;

import io.zerows.sdk.security.Acl;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AclBase implements Acl.View {
    protected final String field;
    private boolean depend = false;     // Default is no depend
    private boolean readOnly = false;   // Default is false

    public AclBase(final String field, final boolean readOnly) {
        this.field = field;
        this.readOnly = readOnly;
    }

    @Override
    public String field() {
        return this.field;
    }

    @Override
    public boolean isDepend() {
        return this.depend;
    }

    @Override
    public Acl.View depend(final boolean depend) {
        this.depend = depend;
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isEdit() {
        return !this.readOnly;
    }
}
