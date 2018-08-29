package org.rebecalang.modeltransformer.akka.corerebeca;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;

import javax.lang.model.element.Modifier;

import static org.rebecalang.modeltransformer.akka.corerebeca.Utils.PACKAGE_NAME;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactiveClass {

    private static Map<String, Class<?>> types = new HashMap<>();
    static {
        types.put("int", Integer.TYPE);
        types.put("boolean", Boolean.TYPE);
        types.put("byte", Byte.TYPE);
    }

    private ReactiveClassDeclaration reactiveClass ;
    private StatementTransformer statementTransformer;
    private ExpressionTransformer expressionTransformer;
    private List<String> messages;
    private List<String> actors;
	private File destination;

    public ReactiveClass(ReactiveClassDeclaration reactiveClass, File rebecaFile, File destination) {
        this.reactiveClass = reactiveClass;
        this.destination = destination;

        final Map<String, String > knownRebecs = new HashMap<>();
        if (reactiveClass.getKnownRebecs() != null) {
            for (FieldDeclaration field : reactiveClass.getKnownRebecs()) {
                for (VariableDeclarator variable : field.getVariableDeclarators()) {
                    knownRebecs.put(variable.getVariableName(), TypesUtilities.getTypeName(field.getType()));
                }
            }
        }

        expressionTransformer = new ExpressionTransformer(knownRebecs);
        statementTransformer = new StatementTransformer(expressionTransformer);
        messages = new ArrayList<>();
        actors = new ArrayList<>();
    }

    public void transform() throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
        if (reactiveClass.getMsgsrvs() != null) {

            final TypeSpec.Builder reactiveClassBuilder = TypeSpec.classBuilder(reactiveClass.getName())
                    .addModifiers(Modifier.PUBLIC)
                    .addField(String.class, "message", Modifier.PRIVATE, Modifier.FINAL)
                    .superclass(akka.actor.AbstractActor.class);

            MethodSpec props = MethodSpec.methodBuilder("props")
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                    .addParameter(String.class, "message")
                    .returns(akka.actor.Props.class)
                    .addStatement("return Props.create(" + reactiveClass.getName() + ".class, () -> new " + reactiveClass.getName() + "(message))")
                    .build();
            reactiveClassBuilder.addMethod(props);

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(String.class, "message")
                    .addStatement("this.message = message")
                    .build();
            reactiveClassBuilder.addMethod(constructor);

            for (FieldDeclaration kr : reactiveClass.getKnownRebecs()) {
                reactiveClassBuilder.addFields(knownRebec(kr));
            }

            for (FieldDeclaration sv : reactiveClass.getStatevars()) {
                reactiveClassBuilder.addFields(stateVar(sv));
            }

            for (MsgsrvDeclaration msgsrv : reactiveClass.getMsgsrvs()) {
                messages.add(msgsrv.getName());
                reactiveClassBuilder.addMethod(msgsrv(msgsrv));
            }

            reactiveClassBuilder.addMethod(createReceive());

            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, reactiveClassBuilder.build())
                    .build();

            Utils.writeToFile(destination, javaFile);

        }
        knownActors();
    }

    private MethodSpec msgsrv(MsgsrvDeclaration msgsrv) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
//        System.out.println(msgsrv.getName());

        MethodSpec.Builder msgsrvBuilder = MethodSpec.methodBuilder(msgsrv.getName())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        if (msgsrv.getFormalParameters() != null) {
            for (FormalParameterDeclaration formalParameter : msgsrv.getFormalParameters()) {
                msgsrvBuilder.addParameter(types.get(((OrdinaryPrimitiveType)formalParameter.getType()).getName()), formalParameter.getName());
            }
        }

        msgsrvBuilder.addCode(statementTransformer.resolveBlockStatement(msgsrv.getBlock()));
        return msgsrvBuilder.build();
    }

    private List<FieldSpec> knownRebec(FieldDeclaration field) {
        List<FieldSpec> fields = new ArrayList<>();

        for (VariableDeclarator variable : field.getVariableDeclarators()) {
            fields.add(FieldSpec.builder(akka.actor.ActorRef.class, variable.getVariableName())
                    .addModifiers(Modifier.PRIVATE)
                    .build());
            actors.add(variable.getVariableName());
        }

        return fields;
    }

    private List<FieldSpec> stateVar(FieldDeclaration field) {
        List<FieldSpec> fields = new ArrayList<>();

        for (VariableDeclarator variable : field.getVariableDeclarators()) {
            fields.add(FieldSpec.builder(types.get(((OrdinaryPrimitiveType)field.getType()).getName()), variable.getVariableName())
                    .addModifiers(Modifier.PRIVATE)
                    .build());
        }

        return fields;
    }

    private MethodSpec createReceive() {
        StringBuilder retValue = new StringBuilder();

        retValue.append(".match(").append(reactiveClass.getName()).append("KnownActors.class, m -> {\n");
        for (String actor : actors) {
            retValue.append("\tthis.")
                    .append(actor)
                    .append(" = m.get")
                    .append(actor.substring(0, 1).toUpperCase()).append(actor.substring(1))
                    .append("();\n");
        }
        retValue.append("})\n");

        for (String message : messages) {
            retValue.append(".match(Messages.").append(reactiveClass.getName()).append(".").append(message).append(".class, m -> ").append(message).append("())\n");
        }

        return MethodSpec.methodBuilder("createReceive")
                .addAnnotation(Override.class)
                .returns(AbstractActor.Receive.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return receiveBuilder()\n" + retValue + ".build()")
                .build();
    }

    private void knownActors() {
        final TypeSpec.Builder knownActorsBuilder = TypeSpec.classBuilder(reactiveClass.getName() + "KnownActors");
        for (String actor : actors) {
            knownActorsBuilder.addField(FieldSpec.builder(akka.actor.ActorRef.class, actor)
                    .addModifiers(Modifier.PRIVATE)
                    .build());
        }

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (String actor : actors) {
            constructorBuilder.addParameter(ActorRef.class, actor)
                    .addStatement("this.$N = $N", actor, actor);
        }
        knownActorsBuilder.addMethod(constructorBuilder.build());

        for (String actor : actors) {
            MethodSpec getter = MethodSpec.methodBuilder("get" + actor.substring(0, 1).toUpperCase() + actor.substring(1))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ActorRef.class)
                    .addStatement("return this.$N", actor)
                    .build();
            knownActorsBuilder.addMethod(getter);
        }

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, knownActorsBuilder.build())
                .build();

        Utils.writeToFile(destination, javaFile);
    }
}