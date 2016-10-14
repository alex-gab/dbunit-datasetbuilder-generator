package org.dbunit.dataset.builder.processors;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;

public final class EntityAnnotatedClass {
    private final TypeElement entityAnnotatedElement;
    private final String builderName;

    public EntityAnnotatedClass(TypeElement entityAnnotatedElement) {
        this.entityAnnotatedElement = entityAnnotatedElement;
        this.builderName = entityAnnotatedElement.getSimpleName() + "DataSetBuilder";
    }

    public final void generateCode(Elements elementUtils, Filer filer, Messager messager) throws IOException {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                "generating code for  " + entityAnnotatedElement.toString());

        final String packageName = "com.other";
                elementUtils.
                getPackageOf(entityAnnotatedElement).
                getQualifiedName().
                toString();
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                "package is  " + packageName);

        final TypeSpec typeSpec = TypeSpec.classBuilder(builderName).
                addModifiers(Modifier.PUBLIC).
                build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }
}
