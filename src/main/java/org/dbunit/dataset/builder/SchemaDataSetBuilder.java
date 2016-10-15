package org.dbunit.dataset.builder;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public final class SchemaDataSetBuilder implements ISchemaDataSetBuilder {
    private final DataSetBuilder dataSetBuilder;

    private SchemaDataSetBuilder() throws DataSetException {
        this.dataSetBuilder = new DataSetBuilder();
    }

    public final EmpClerkSchemaDataRowBuilder newEmpClerkRow() {
        return new EmpClerkSchemaDataRowBuilder(this, "EMP_CLERK");
    }

    @Override
    public final IDataSet build() throws DataSetException {
        return dataSetBuilder.build();
    }

    DataSetBuilder getUndelyingBuilder() {
        return dataSetBuilder;
    }
}
