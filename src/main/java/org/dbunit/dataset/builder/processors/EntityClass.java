package org.dbunit.dataset.builder.processors;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;

public final class EntityClass {
    private final CreateTable statement;

    public EntityClass(CreateTable statement) {
        this.statement = statement;
    }

    public final void generateCode(Elements elementUtils, Filer filer, Messager messager) throws IOException {
        final String tableName = statement.getTable().getName();
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                "generating code for  " + tableName);

        final String packageName = "com.other";

        final TypeSpec typeSpec = TypeSpec.classBuilder(tableName + "DataSetBuilder").
                addModifiers(Modifier.PUBLIC).
                build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }
}
