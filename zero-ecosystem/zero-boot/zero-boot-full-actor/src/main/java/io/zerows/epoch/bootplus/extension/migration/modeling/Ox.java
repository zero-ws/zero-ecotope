package io.zerows.epoch.bootplus.extension.migration.modeling;

import io.zerows.support.Ut;
import io.zerows.extension.mbse.basement.domain.tables.daos.MAttributeDao;
import io.zerows.extension.mbse.basement.domain.tables.daos.MEntityDao;
import io.zerows.extension.mbse.basement.domain.tables.daos.MFieldDao;
import io.zerows.extension.mbse.basement.domain.tables.daos.MModelDao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {
    ConcurrentMap<Class<?>, Revision> POOL = new ConcurrentHashMap<Class<?>, Revision>() {
        {
            this.put(MModelDao.class, Ut.instance(ModelRevision.class));
            this.put(MEntityDao.class, Ut.instance(EntityRevision.class));
            this.put(MFieldDao.class, Ut.instance(FieldRevision.class));
            this.put(MAttributeDao.class, Ut.instance(AttributeRevision.class));
        }
    };
}
