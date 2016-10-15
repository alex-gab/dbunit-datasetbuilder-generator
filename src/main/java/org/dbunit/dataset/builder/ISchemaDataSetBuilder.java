package org.dbunit.dataset.builder;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public interface ISchemaDataSetBuilder {
    IDataSet build() throws DataSetException;
}
