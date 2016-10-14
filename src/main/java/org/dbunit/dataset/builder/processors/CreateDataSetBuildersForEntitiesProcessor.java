package org.dbunit.dataset.builder.processors;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import org.dbunit.dataset.builder.annotations.CreateDataSetBuildersForEntities;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public final class CreateDataSetBuildersForEntitiesProcessor extends AbstractProcessor {
    private static final Class<CreateDataSetBuildersForEntities> ANNOTATION_CLASS =
            CreateDataSetBuildersForEntities.class;
    private static final Class<Entity> ENTITY_CLASS = Entity.class;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private Set<EntityAnnotatedClass> entityAnnotatedClasses = new HashSet<>();

    @Override
    public final synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(ANNOTATION_CLASS.getCanonicalName());
    }

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                "enter annotations!!!!" + annotations);
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                "enter!!!!" + roundEnv.getRootElements());
        try {
            final Set<? extends Element> elementsAnnotated = roundEnv.getElementsAnnotatedWith(ANNOTATION_CLASS);
            if (!elementsAnnotated.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "found " + CreateDataSetBuildersForEntities.class.toString());
                checkThatOnlyClassesAreAnnotated(elementsAnnotated);

                final Properties properties = new Properties();
                final InputStream propertiesFile = getClass().getResourceAsStream("/application.properties");
                properties.load(propertiesFile);
                propertiesFile.close();
                final String entityClassName = properties.getProperty("entity.classes");
                final Class<?> entityClass = Class.forName(entityClassName);
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "found entities with fields" + Arrays.asList(entityClass.getDeclaredFields()).toString());


                for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ENTITY_CLASS)) {
                    final TypeElement typeElement = (TypeElement) annotatedElement;
                    final EntityAnnotatedClass annotatedClass = new EntityAnnotatedClass(typeElement);
                    entityAnnotatedClasses.add(annotatedClass);
                    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                            annotatedClass.toString());
                }
                if (entityAnnotatedClasses.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                            "not found!!!!");
                }
                for (EntityAnnotatedClass entityAnnotatedClass : entityAnnotatedClasses) {
                    entityAnnotatedClass.generateCode(elementUtils, filer, messager);
                }
                entityAnnotatedClasses.clear();
            }
        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            error(null, e.getMessage());
        }
        return true;
    }

    private void checkThatOnlyClassesAreAnnotated(Set<? extends Element> elements) throws ProcessingException {
        for (Element annotatedElement : elements) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                throw new ProcessingException(annotatedElement,
                        "Only classes can be annotated with @%s",
                        ANNOTATION_CLASS.getSimpleName());
            }
        }
    }

    private void error(Element element, String message) {
        messager.printMessage(ERROR,
                message,
                element);
    }
}
