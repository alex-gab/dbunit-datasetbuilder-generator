package org.dbunit.dataset.builder.processors;

import com.squareup.javapoet.*;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.builder.ColumnSpec;
import org.dbunit.dataset.builder.DataSetBuilder;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import java.io.IOException;

import static javax.lang.model.element.Modifier.*;

public final class EntityClass {
    public static final String BUILDER_CLASS_PREFIX = "DataSetBuilder";

    private final CreateTable statement;

    public EntityClass(CreateTable statement) {
        this.statement = statement;
    }

    public final void generateCode(Elements elementUtils, Filer filer, Messager messager) throws CreateDataSetBuildersException {
        try {
            final String tableName = statement.getTable().getName();
            final String className = tableName + BUILDER_CLASS_PREFIX;
            final String packageName = "com.other";
            final ClassName returnType = ClassName.get(packageName, className);

            try {
                DataSetBuilder builder = new DataSetBuilder();
                builder.newRow("PERSON").with("NAME", "Bob").with("AGE", 18).add();
//                builder.add(new BasicDataRowBuilder());
            } catch (DataSetException e) {
                e.printStackTrace();
            }

            final TypeSpec.Builder typeSpecBuilder = TypeSpec.
                    classBuilder(className).
                    addModifiers(PUBLIC, FINAL).
                    addField(DataSetBuilder.class, "underlyingBuilder", PRIVATE, FINAL).
                    addMethod(
                            MethodSpec.constructorBuilder().
                                    addModifiers(PRIVATE).
                                    addParameter(DataSetBuilder.class, "underlyingBuilder", FINAL).
                                    addStatement("this.$L = $L", "underlyingBuilder", "underlyingBuilder").
                                    build()
                    ).
                    addMethod(
                            MethodSpec.methodBuilder("a" + tableName + "DataSet").
                                    addModifiers(PUBLIC, STATIC).
                                    addException(DataSetException.class).
                                    returns(returnType).
                                    addStatement("return new $L(new $T())", className, DataSetBuilder.class).
                                    build()
                    );

            for (ColumnDefinition columnDefinition : statement.getColumnDefinitions()) {
                final String dataType = columnDefinition.getColDataType().getDataType();
                final String name = columnDefinition.getColumnName();
                final Class clazz = SqlTypes.valueOf(dataType).getJavaClass();

                TypeName columnSpec = ParameterizedTypeName.get(ColumnSpec.class, clazz);

                typeSpecBuilder.
                        addField(
                                FieldSpec.builder(columnSpec, name, PRIVATE).
                                        initializer("ColumnSpec.newColumn($S)", name).
                                        build()
                        ).
                        addMethod(
                                MethodSpec.methodBuilder(name).
                                        addModifiers(PUBLIC, FINAL).
                                        returns(returnType).
                                        addParameter(clazz, name, FINAL).
                                        addStatement("this.$L = $L", name, name).
                                        addStatement("return this").
                                        build()
                        );
            }


            final TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
        } catch (IOException e) {
            throw new CreateDataSetBuildersException("Code generation exception: ", e);
        }
    }
}
