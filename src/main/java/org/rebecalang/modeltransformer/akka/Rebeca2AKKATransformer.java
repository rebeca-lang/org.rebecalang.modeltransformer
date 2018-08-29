package org.rebecalang.modeltransformer.akka;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.akka.corerebeca.ExpressionTransformer;
import org.rebecalang.modeltransformer.akka.corerebeca.ReactiveClass;
import org.rebecalang.modeltransformer.akka.corerebeca.UnsupportedExpressionException;
import org.rebecalang.modeltransformer.akka.corerebeca.UnsupportedStatementException;
import org.rebecalang.modeltransformer.akka.corerebeca.Utils;

import javax.lang.model.element.Modifier;

import static org.rebecalang.modeltransformer.akka.corerebeca.Utils.PACKAGE_NAME;

import java.io.File;
import java.util.*;

public class Rebeca2AKKATransformer {
    private static Rebeca2AKKATransformer instance = new Rebeca2AKKATransformer();

    public static Rebeca2AKKATransformer getInstance() {
        return instance;
    }

    private File destination;

	public void transformModel(File rebecaFile, File destination, Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> analysisFeatures, CommandLine commandLine) throws ExceptionContainer {
		this.destination = destination;
		
        RebecaCompiler compiler = new RebecaCompiler();
        compiler.compileRebecaFile(rebecaFile, compilerFeatures);

        if (compiler.getExceptionContainer().getExceptions().size() != 0) {
            throw compiler.getExceptionContainer();
        }

        RebecaModel model = compiler.syntaxCheckRebecaFile(rebecaFile, compilerFeatures);

        if (model.getRebecaCode().getReactiveClassDeclaration() != null) {
            for (ReactiveClassDeclaration reactiveClass : model.getRebecaCode().getReactiveClassDeclaration()) {
                try {
					new ReactiveClass(reactiveClass, rebecaFile, destination).transform();
				} catch (CodeCompilationException | UnsupportedStatementException | UnsupportedExpressionException e) {
					ExceptionContainer ec = new ExceptionContainer();
					ec.addException(e);
					throw ec;
				}
            }
        }

        try {
			main(model.getRebecaCode().getMainDeclaration());
		} catch (UnsupportedExpressionException e) {
			ExceptionContainer ec = new ExceptionContainer();
			ec.addException(e);
			throw ec;
		}
        messages(model.getRebecaCode().getReactiveClassDeclaration());
        injector(model.getRebecaCode().getReactiveClassDeclaration());
    }

    private void main(MainDeclaration mainDeclaration) throws UnsupportedExpressionException {

        MethodSpec.Builder mainBuilder = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("final $T system = ActorSystem.create($S)", akka.actor.ActorSystem.class, "Rebeca");

        for (MainRebecDefinition mainRebec : mainDeclaration.getMainRebecDefinition()) {
            mainBuilder.addStatement("final $T $N = system.actorOf($N.props($S), $S)", akka.actor.ActorRef.class, mainRebec.getName(), ((OrdinaryPrimitiveType)mainRebec.getType()).getName(), mainRebec.getName(), mainRebec.getName());
        }

        for (MainRebecDefinition mainRebec : mainDeclaration.getMainRebecDefinition()) {
            StringBuilder bindings = new StringBuilder("");
            ExpressionTransformer expressionTransformer = new ExpressionTransformer(new HashMap<String, String>());
            for (int i=0 ; i<mainRebec.getBindings().size(); i++) {
                Expression expression = mainRebec.getBindings().get(i);
                bindings.append(expressionTransformer.translate(expression));
                if (i < mainRebec.getBindings().size() - 1)
                    bindings.append(", ");
            }
            mainBuilder.addStatement("Injector.inject($N, new $NKnownActors($N))", mainRebec.getName(), ((OrdinaryPrimitiveType)mainRebec.getType()).getName(), bindings.toString());
        }

        for (MainRebecDefinition mainRebec : mainDeclaration.getMainRebecDefinition()) {
            mainBuilder.addStatement("$N.tell(new Messages.$N.initial(), $T.noSender())", mainRebec.getName(), ((OrdinaryPrimitiveType)mainRebec.getType()).getName(), akka.actor.ActorRef.class);
        }

        TypeSpec mainClass = TypeSpec.classBuilder("Main")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(mainBuilder.build())
//                .addField(system)
                .build();

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME , mainClass)
                .build();

        Utils.writeToFile(destination, javaFile);
    }

    private void messages(List<ReactiveClassDeclaration> classes) {

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        TypeSpec.Builder messagesClass = TypeSpec.classBuilder("Messages")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor);
//                .addField(system);

        for (ReactiveClassDeclaration rcd : classes) {

            ClassName reactiveClassName = ClassName.get(PACKAGE_NAME, rcd.getName());
            TypeSpec.Builder reactiveClassBuilder = TypeSpec.classBuilder(reactiveClassName.simpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

            for (MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
                ClassName messageName = ClassName.get(PACKAGE_NAME, msgsrv.getName());
                reactiveClassBuilder.addType(TypeSpec.classBuilder(messageName.simpleName())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .build());
            }

            messagesClass.addType(reactiveClassBuilder.build());
        }

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, messagesClass.build())
                .build();

        Utils.writeToFile(destination, javaFile);
    }

    private void injector(List<ReactiveClassDeclaration> classes) {
        TypeSpec.Builder injectorBuilder = TypeSpec.classBuilder("Injector")
                .addModifiers(Modifier.PUBLIC);

        for (ReactiveClassDeclaration rcd : classes) {
            ClassName reactiveClassName = ClassName.get(PACKAGE_NAME, rcd.getName() + "KnownActors");
            MethodSpec inject = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(akka.actor.ActorRef.class, "actor")
                    .addParameter(reactiveClassName, "args")
                    .addStatement("actor.tell(args, ActorRef.noSender())")
                    .build();

            injectorBuilder.addMethod(inject);
        }

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, injectorBuilder.build())
                .build();

        Utils.writeToFile(destination, javaFile);
    }


}
