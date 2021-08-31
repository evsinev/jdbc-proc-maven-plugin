package com.payneteasy.jdbcproc.plugin.maven;

import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.ResultSetColumnInfo;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.StoredProcedureArgumentInfo;
import com.googlecode.jdbcproc.daofactory.impl.procedureinfo.StoredProcedureInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ProcedureWriter {

    private static final Logger       LOG   = LoggerFactory.getLogger(ProcedureWriter.class);
    private static final SqlDataTypes TYPES = new SqlDataTypes();

    private final PrintWriter         out;

    public ProcedureWriter(File file) throws FileNotFoundException {
        out = new PrintWriter(file);
    }

    public void write(StoredProcedureInfo procedure) {

        String procedureName   = procedure.getProcedureName();
        String procedureSpaces = getProcedureSpaces(procedureName);

        printf("drop procedure if exists %s;", procedureName);
        printf("delimiter $$");
        printf("create procedure %s(", procedureName);

        writeArguments(procedure.getArguments(), procedureSpaces);

        printf("                 %s)", procedureSpaces);
        printf("  begin");

        writeSelect(procedure.getResultSetColumns());

        printf("  end");
        printf("$$");
        printf("delimiter ;");

        writeResult(procedure.getResultSetColumns(), procedureName);

        out.close();
    }

    private void writeSelect(List<ResultSetColumnInfo> aColumns) {
        if(aColumns.size() == 0) {
            printf("    --");
            return;
        }

        int maxLength = aColumns.stream().map(ResultSetColumnInfo::getColumnName).mapToInt(String::length).max().orElse(0);

        printf("    select");
        for (int i = 0; i < aColumns.size(); i++) {
            ResultSetColumnInfo column = aColumns.get(i);
            String comma = i < aColumns.size() - 1 ? "," : "";
            printf("        %s %s%s", TYPES.getExampleValue(column.getDataType()), column.getColumnName(), comma);
        }
        printf("    ;");
    }

    private void writeResult(List<ResultSetColumnInfo> aColumns, String aName) {
        if(aColumns.size() == 0) {
            return;
        }

        printf("call save_routine_information('%s',", aName);
        printf("                              concat_ws(',',");

        int maxLength = aColumns.stream().map(ResultSetColumnInfo::getColumnName).mapToInt(String::length).max().orElse(0);

        for (int i = 0; i < aColumns.size(); i++) {
            ResultSetColumnInfo column = aColumns.get(i);
            String comma = i < aColumns.size() - 1 ? "," : "";
            printf("                                       '%s %s'%s", padRight(column.getColumnName(), maxLength), TYPES.getResultTypeName(column.getDataType()), comma);
        }

        printf("                                       )");
        printf("                             );");
    }

    private void writeArguments(List<StoredProcedureArgumentInfo> arguments, String procedureSpaces) {
        int maxLength = arguments.stream().map(StoredProcedureArgumentInfo::getColumnName).mapToInt(String::length).max().orElse(0);
        for(int i=0; i<arguments.size(); i++) {
            String comma = i < arguments.size() - 1 ? "," : "";
            StoredProcedureArgumentInfo argument = arguments.get(i);
            printf("                 %s i_%s %s%s", procedureSpaces, padRight(argument.getColumnName(), maxLength), TYPES.getArgumentTypeName(argument.getDataType()), comma);
        }
    }

    private String padRight(String columnName, int maxLength) {
        StringBuilder sb = new StringBuilder(maxLength);
        sb.append(columnName);
        while(sb.length() < maxLength) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private String getProcedureSpaces(String procedureName) {
        StringBuilder sb = new StringBuilder(procedureName.length());
        while(sb.length() < procedureName.length()) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private void printf(String aTemplate, Object ... args) {
        out.printf(aTemplate + "\n", args);
    }
}
