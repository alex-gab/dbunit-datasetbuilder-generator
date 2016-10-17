package org.dbunit.dataset.builder.processors;

import com.squareup.javapoet.*;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.builder.AbstractSchemaDataRowBuilder;
import org.dbunit.dataset.builder.AbstractSchemaDataSetBuilder;
import org.dbunit.dataset.builder.ColumnSpec;
import org.dbunit.dataset.builder.DataSetBuilder;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import java.io.IOException;

import static javax.lang.model.element.Modifier.*;

public final class EntityClass {
    public static final String BUILDER_CLASS_PREFIX = "SchemaDataRowBuilder";

    private final CreateTable statement;

    public EntityClass(CreateTable statement) {
        this.statement = statement;
    }

    public final void generateCode(Elements elementUtils, Filer filer, Messager messager) throws CreateDataSetBuildersException {
        try {
            final String tableName = statement.getTable().getName();
            final String rowBuilderClassName = tableName + BUILDER_CLASS_PREFIX;
            final String packageName = "org.dbunit.dataset.builder";
            final ClassName rowBuilderClass = ClassName.get(packageName, rowBuilderClassName);

            final TypeSpec.Builder dataSetBuilder = TypeSpec.classBuilder("SchemaDataSetBuilder").
                    addModifiers(PUBLIC, FINAL).
                    superclass(AbstractSchemaDataSetBuilder.class).
                    addMethod(
                            MethodSpec.constructorBuilder().
                                    addModifiers(PUBLIC).
                                    addException(DataSetException.class).
                                    addStatement("super(new $T())", DataSetBuilder.class).
                                    build()
                    ).
                    addMethod(
                            MethodSpec.methodBuilder("new" + tableName + "Row").
                                    addModifiers(PUBLIC, FINAL).
                                    returns(rowBuilderClass).
                                    addStatement("return new $T($L, $S)", rowBuilderClass, "this", tableName).
                                    build()
                    );

            final ClassName schemaDataSetBuilderClassName = ClassName.get(packageName, "SchemaDataSetBuilder");
            ParameterizedTypeName superclass =
                    ParameterizedTypeName.get(
                            ClassName.get(AbstractSchemaDataRowBuilder.class),
                            schemaDataSetBuilderClassName);
            final TypeSpec.Builder dataRowBuilder = TypeSpec.classBuilder(rowBuilderClassName).
                    addModifiers(PUBLIC, FINAL).
                    superclass(superclass).
                    addMethod(
                            MethodSpec.constructorBuilder().
                                    addParameter(schemaDataSetBuilderClassName, "schemaDataSetBuilder", FINAL).
                                    addParameter(String.class, "tableName", FINAL).
                                    addStatement("super($L, $L)", "schemaDataSetBuilder", "tableName").
                                    build()
                    );


//            final TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className).
//                    addModifiers(PUBLIC, FINAL).
//                    addField(DataSetBuilder.class, "underlyingBuilder", PRIVATE, FINAL).
//                    addMethod(
//                            MethodSpec.constructorBuilder().
//                                    addModifiers(PRIVATE).
//                                    addParameter(DataSetBuilder.class, "underlyingBuilder", FINAL).
//                                    addStatement("this.$L = $L", "underlyingBuilder", "underlyingBuilder").
//                                    build()
//                    ).
//                    addMethod(
//                            MethodSpec.methodBuilder("a" + tableName + "DataSet").
//                                    addModifiers(PUBLIC, STATIC).
//                                    addException(DataSetException.class).
//                                    returns(returnType).
//                                    addStatement("return new $L(new $T())", className, DataSetBuilder.class).
//                                    build()
//                    );

            for (ColumnDefinition columnDefinition : statement.getColumnDefinitions()) {
                final String dataType = columnDefinition.getColDataType().getDataType();
                final String name = columnDefinition.getColumnName();
                final Class clazz = SqlTypes.valueOf(dataType).getJavaClass();

                TypeName columnSpec = ParameterizedTypeName.get(ColumnSpec.class, clazz);

                final FieldSpec field = FieldSpec.builder(columnSpec, name, PRIVATE, STATIC, FINAL).
                        initializer("ColumnSpec.newColumn($S)", name).
                        build();
                dataRowBuilder.
                        addField(field).
                        addMethod(
                                MethodSpec.methodBuilder(name).
                                        addModifiers(PUBLIC, FINAL).
                                        returns(rowBuilderClass).
                                        addParameter(clazz, name, FINAL).
                                        addStatement("$L.with($L.$N, $L)", "dataRowBuilder", rowBuilderClassName, field, name).
                                        addStatement("return this").
                                        build()
                        );
            }

            JavaFile.builder(packageName, dataSetBuilder.build()).build().writeTo(filer);
            JavaFile.builder(packageName, dataRowBuilder.build()).build().writeTo(filer);
            System.out.println("generated builders");
        } catch (IOException e) {
            throw new CreateDataSetBuildersException("Code generation exception: ", e);
        }
    }
}
