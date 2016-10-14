package org.dbunit.dataset.builder.annotations;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.*;

public final class Main {
    public static void main(String[] args) throws IOException, JSQLParserException {
        String[] sqlStatements = readSql("schema.sql");

        for (String sqlStatement : sqlStatements) {
            CreateTable statement = (CreateTable) CCJSqlParserUtil.parse(new StringReader(sqlStatement));
            System.out.println(statement);
        }
    }


    public static String[] readSql(String schema) throws IOException {
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
