package org.dbunit.dataset.builder;

import org.dbunit.dataset.DataSetException;

import java.util.Date;

public final class EmpClerkSchemaDataRowBuilder implements SchemaDataRowBuilder {
    private final DataRowBuilder dataRowBuilder;
    private final SchemaDataSetBuilder dataSetBuilder;

    private ColumnSpec<String> ENAME = ColumnSpec.newColumn("ENAME");

    private ColumnSpec<Date> HIREDATE = ColumnSpec.newColumn("HIREDATE");

    private ColumnSpec<Integer> SAL = ColumnSpec.newColumn("SAL");

    protected EmpClerkSchemaDataRowBuilder(SchemaDataSetBuilder schemaDataSetBuilder, String tableName) {
        this.dataSetBuilder = schemaDataSetBuilder;
        dataRowBuilder = new DataRowBuilder(dataSetBuilder.getUndelyingBuilder(), tableName);
    }

    public EmpClerkSchemaDataRowBuilder ENAME(String ENAME) {
        dataRowBuilder.with(this.ENAME, ENAME);
        return this;
    }

    public EmpClerkSchemaDataRowBuilder HIREDATE(Date HIREDATE) {
        dataRowBuilder.with(this.HIREDATE, HIREDATE);
        return this;
    }

    public EmpClerkSchemaDataRowBuilder SAL(Integer SAL) {
        dataRowBuilder.with(this.SAL, SAL);
        return this;
    }

    @Override
    public final SchemaDataSetBuilder add() throws DataSetException {
        dataSetBuilder.getUndelyingBuilder().add(dataRowBuilder);
        return dataSetBuilder;
    }
}
