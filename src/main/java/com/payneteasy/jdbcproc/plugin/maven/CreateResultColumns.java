package com.payneteasy.jdbcproc.plugin.maven;

import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.ResultSetColumnInfo;

import javax.persistence.Column;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.payneteasy.jdbcproc.plugin.maven.ClassUtils.getAllMethods;

public class CreateResultColumns {

    private final SqlDataTypes dataTypes = new SqlDataTypes();

    List<ResultSetColumnInfo> createColumns(String aPrefix, Class aClass) {
        if(aClass == void.class) {
            return Collections.emptyList();
        }

        return getAllMethods(aClass).stream()
                .filter(method -> method.isAnnotationPresent(Column.class))
                .map(method -> {
                    Column column = method.getAnnotation(Column.class);
                     return new ResultSetColumnInfo(aPrefix + column.name(), dataTypes.findArgumentDataType(method.getReturnType()));
                })
                .collect(Collectors.toList());

    }
}
