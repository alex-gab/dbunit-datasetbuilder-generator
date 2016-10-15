package org.dbunit.dataset.builder.processors;

public final class SqlParsingException extends CreateDataSetBuildersException {
    public SqlParsingException(Throwable cause, String msg, Object... args) {
        super(String.format(msg, args), cause);
    }
}
