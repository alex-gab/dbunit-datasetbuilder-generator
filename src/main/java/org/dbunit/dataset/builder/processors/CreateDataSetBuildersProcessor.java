package org.dbunit.dataset.builder.processors;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.dbunit.dataset.builder.annotations.CreateDataSetBuilders;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public final class CreateDataSetBuildersProcessor extends AbstractProcessor {
    private static final Class<CreateDataSetBuilders> ANNOTATION_CLASS =
            CreateDataSetBuilders.class;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

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
        try {
            final Set<? extends Element> elementsAnnotated = roundEnv.getElementsAnnotatedWith(ANNOTATION_CLASS);
            if (!elementsAnnotated.isEmpty()) {
                checkThatOnlyClassesAreAnnotated(elementsAnnotated);

                List<EntityClass> entityClasses = retrieveEntityClasses("/Users/alex-mac/IntelliJ/IntelliJ-Workspace/JdbcTutorial/src/test/resources/schema.sql");


                for (EntityClass entityClass : entityClasses) {
                    entityClass.generateCode(elementUtils, filer, messager);
                }
                entityClasses.clear();
            }
        } catch (AnnotationProcessingException e) {
            messager.printMessage(ERROR,
                    e.getMessage(),
                    e.getElement());
        } catch (CreateDataSetBuildersException e) {
            messager.printMessage(ERROR, e.getMessage());
        }
        return true;
    }

    private void checkThatOnlyClassesAreAnnotated(Set<? extends Element> elements) throws AnnotationProcessingException {
        for (Element annotatedElement : elements) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                throw new AnnotationProcessingException(annotatedElement,
                        "Only classes can be annotated with @%s",
                        ANNOTATION_CLASS.getSimpleName());
            }
        }
    }

    private List<EntityClass> retrieveEntityClasses(String schemaFileName) throws SqlParsingException {
        List<EntityClass> entityClasses = new ArrayList<>();
        try {
            String[] sqlStatements = readSql(schemaFileName);
            for (String sqlStatement : sqlStatements) {
                CreateTable statement = (CreateTable) CCJSqlParserUtil.parse(new StringReader(sqlStatement));
                entityClasses.add(new EntityClass(statement));
            }
        } catch (JSQLParserException | IOException e) {
            throw new SqlParsingException(e, "Could not parse sql schema");
        }
        return entityClasses;
    }

    private String[] readSql(String schema) throws IOException {
        System.out.println("schema file name is " + schema);
        final InputStream schemaStream = new FileInputStream(schema);
        System.out.println("schema stream is " + schemaStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(schemaStream));
        String mysql = "";
        String line;
        while ((line = br.readLine()) != null) {
            mysql = mysql + line;
        }
        br.close();
        mysql = mysql.replaceAll("`", "");
        return mysql.split(";");
    }

}
