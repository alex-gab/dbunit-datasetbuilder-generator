package org.dbunit.dataset.builder.annotations;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.builder.ColumnSpec;
import org.dbunit.dataset.builder.DataSetBuilder;

public final class Main {
    public static void main(String[] args) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        ColumnSpec<Integer> age = ColumnSpec.newColumn("AGE");
    }
}
