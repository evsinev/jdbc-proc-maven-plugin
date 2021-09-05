package com.payneteasy.jdbcproc.plugin.maven;

import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.ResultSetColumnInfo;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.payneteasy.jdbcproc.plugin.maven.ClassUtils.getAllMethods;

public class CreateOneToOne {

    private final CreateResultColumns createColumns = new CreateResultColumns();

    List<ResultSetColumnInfo> createOneToOneColumns(Class aClass) {
        List<ResultSetColumnInfo> list = new ArrayList<>();

        for (Method method : getAllMethods(aClass)) {
            if(!method.isAnnotationPresent(OneToOne.class)) {
                continue;
            }

            JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                throw new IllegalStateException("No JoinColumn annotation for method " + method);
            }

            list.addAll(createColumns.createColumns(joinColumn.table() + "_", method.getReturnType()));

        }

        return list;
    }
}
