package org.dbunit.dataset.builder;

import org.dbunit.dataset.DataSetException;

public interface SchemaDataRowBuilder {
    ISchemaDataSetBuilder add() throws DataSetException;
}
