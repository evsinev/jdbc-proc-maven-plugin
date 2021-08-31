package com.payneteasy.jdbcproc.plugin.maven;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqlDataTypes {

    private final Map<Class, Integer>  dataTypes     = new HashMap<>();
    private final Map<Integer, String> argumentNames = new HashMap<>();
    private final Map<Integer, String> resultNames   = new HashMap<>();
    private final Map<Integer, String> examples      = new HashMap<>();

    public SqlDataTypes() {
        addType(String.class  , Types.VARCHAR    , "varchar(50)"       , "varchar"   , "''");
        addType(Long.class    , Types.BIGINT     , "int(10)  unsigned" , "int"       , "1");
        addType(long.class    , Types.BIGINT     , "int(10)  unsigned" , "int"       , "1");
        addType(Integer.class , Types.INTEGER    , "int(10)"           , "int"       , "1");
        addType(int.class     , Types.INTEGER    , "int(10)"           , "int"       , "1");
        addType(Date.class    , Types.TIMESTAMP  , "datetime"          , "datetime"  , "'2020.01.01 12:12:12'");
    }

    private void addType(Class aClass, int aType, String aTypeName, String aResultName, String aExample) {
        dataTypes.put(aClass, aType);
        argumentNames.put(aType, aTypeName);
        resultNames.put(aType, aResultName);
        examples.put(aType, aExample);
    }

    public short findArgumentDataType(Class<?> aType) {
        Integer type = dataTypes.get(aType);
        if (type == null) {
            throw new IllegalStateException("No type for " + aType);
        }
        return type.shortValue();
    }

    public String getArgumentTypeName(int aType) {
        String name = argumentNames.get(aType);
        if(name == null) {
            throw new IllegalStateException("No type for " + aType);
        }
        return name;
    }

    public String getResultTypeName(int aType) {
        String name = resultNames.get(aType);
        if(name == null) {
            throw new IllegalStateException("No type for " + aType);
        }
        return name;
    }

    public String getExampleValue(int dataType) {
        return examples.get(dataType);
    }
}
