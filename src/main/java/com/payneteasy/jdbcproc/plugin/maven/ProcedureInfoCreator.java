package com.payneteasy.jdbcproc.plugin.maven;

import com.googlecode.jdbcproc.daofactory.annotation.AMetaLoginInfo;
import com.googlecode.jdbcproc.daofactory.annotation.AStoredProcedure;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.ResultSetColumnInfo;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.StoredProcedureArgumentInfo;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.StoredProcedureInfo;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.googlecode.jdbcproc.daofactory.impl.block.BlockFactoryUtils.isCollectionAssignableFrom;
import static com.payneteasy.jdbcproc.plugin.maven.ClassUtils.getAllMethods;
import static java.util.stream.Collectors.toList;

public class ProcedureInfoCreator {

    private static final short ARGUMENT_IN = (short) StoredProcedureArgumentInfo.IN;

    private final SqlDataTypes dataTypes = new SqlDataTypes();

    private final String loginUsername;
    private final String loginRoleName;

    public ProcedureInfoCreator(String loginUsername, String loginRoleName) {
        this.loginUsername = loginUsername;
        this.loginRoleName = loginRoleName;
    }

    public List<StoredProcedureInfo> createProcedures(Class aClass) {
        return getAllMethods(aClass).stream()
                .filter(method -> method.isAnnotationPresent(AStoredProcedure.class))
                .map(this::createProcedure)
                .collect(toList());
    }

    private StoredProcedureInfo createProcedure(Method aMethod) {
        AStoredProcedure    procedure = aMethod.getAnnotation(AStoredProcedure.class);
        StoredProcedureInfo info      = new StoredProcedureInfo(procedure.name());

        addMetaLogin(info, aMethod);

        for (Class<?> type : aMethod.getParameterTypes()) {
            addArgumentFromType(info, type);
        }
        addReturn(info, fixCollection(aMethod.getReturnType(), aMethod.getGenericReturnType()));
        return info;
    }

    private void addMetaLogin(StoredProcedureInfo aInfo, Method aMethod) {
        if(!aMethod.isAnnotationPresent(AMetaLoginInfo.class)) {
            return;
        }

        aInfo.addColumn(loginUsername, ARGUMENT_IN, dataTypes.findArgumentDataType(String.class));
        aInfo.addColumn(loginRoleName, ARGUMENT_IN, dataTypes.findArgumentDataType(String.class));

    }

    private Class<?> fixCollection(Class aClass, Type returnType) {
        if(isCollectionAssignableFrom(aClass)) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            return (Class)parameterizedType.getActualTypeArguments()[0];
        } else {
            return aClass;
        }
    }

    private void addReturn(StoredProcedureInfo info, Class<?> type) {
        if(type == void.class) {
            return;
        }

        getAllMethods(type).stream()
                .filter(method -> method.isAnnotationPresent(Column.class))
                .forEach(method -> {
                    Column column = method.getAnnotation(Column.class);
                    info.addResultSetColumn(new ResultSetColumnInfo(column.name(), dataTypes.findArgumentDataType(method.getReturnType())));
                });
    }

    private void addArgumentFromType(StoredProcedureInfo info, Class<?> type) {
        getAllMethods(type).stream()
                .filter(method -> method.isAnnotationPresent(Column.class))
                .forEach(method -> {
                    Column column = method.getAnnotation(Column.class);
                    info.addColumn(column.name(), ARGUMENT_IN, dataTypes.findArgumentDataType(method.getReturnType()));
                });
        ;
    }
    
}
