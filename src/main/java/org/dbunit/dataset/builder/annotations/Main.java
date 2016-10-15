package org.dbunit.dataset.builder.annotations;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.dbunit.dataset.builder.processors.SqlParsingException;
import org.dbunit.dataset.builder.processors.SqlTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public final class Main {
    public static void main(String[] args) throws SqlParsingException {
        final List<CreateTable> statements = retrieveEntityClasses("schema.sql");
        for (CreateTable statement : statements) {
            System.out.println(statement.getTable().getName());
            for (ColumnDefinition columnDefinition : statement.getColumnDefinitions()) {
                System.out.println(columnDefinition.getColumnName());
                System.out.println(columnDefinition.getColDataType().getDataType());
            }
        }

        System.out.println(SqlTypes.valueOf("VARCHAR").getJavaClass());
    }

    private static List<CreateTable> retrieveEntityClasses(String schemaFileName) throws SqlParsingException {
        List<CreateTable> statements = new ArrayList<>();
        try {
            String[] sqlStatements = readSql(schemaFileName);
            for (String sqlStatement : sqlStatements) {
                CreateTable statement = (CreateTable) CCJSqlParserUtil.parse(new StringReader(sqlStatement));
                statements.add(statement);
            }
        } catch (JSQLParserException | IOException e) {
            throw new SqlParsingException(e, "Could not parse sql schema");
        }
        return statements;
    }

    private static String[] readSql(String schema) throws IOException {
        final InputStream schemaStream = ClassLoader.getSystemResourceAsStream(schema);
        BufferedReader br = new BufferedReader(new InputStreamReader(schemaStream));
        String mysql = "";
        String line;
        while ((line = br.readLine()) != null) {
            mysql = mysql + line;
        }
        br.close();
        mysql = mysql.replaceAll("`", "");
        return mysql.split(";");
    }


}
