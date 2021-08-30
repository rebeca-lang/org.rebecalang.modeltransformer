package org.rebecalang.modeltransformer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class RebecaToRILInheritanceTest {

    @Autowired
    Rebeca2RILModelTransformer rebeca2RIL;

    @Autowired
    RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    public ExceptionContainer exceptionContainer;

    private String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modeltransformer/testcase/inheritance/";

    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    @Test
    public void useParentStateVarsInChildTest() {
        String modelName = "use_parent_statevars_in_child";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printExceptions();
        printAllMethods(transformModel);
    }

    @Test
    public void generateHomonymousResult() {
        String modelName = "homonymous_attributes";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printExceptions();
        printAllMethods(transformModel);
    }

    @Test
    public void generateInstanceOfResult() {
        String modelName = "instanceof_expression";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printExceptions();
        printAllMethods(transformModel);
    }

    @Test
    public void generateInterfaceAndAbstractResult() {
        String modelName = "interface_abstract";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void generateKnownrebecsSequenceResult() {
        String modelName = "known_rebecs_sequence";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void generateSelfAndNormalCallsResult() {
        String modelName = "self_and_normal_calls";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void generateDynamicPolymorphism() {
        String modelName = "dynamic_polymorphism";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsKnownActorsInChild() {
        String modelName = "use_parents_known_actors_in_child";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsMethodsInChild() {
        String modelName = "use_parents_methods_in_child";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsMsgsrvInChild() {
        String modelName = "use_parents_msgsrv_in_child";
        File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");

        Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
        Pair<RebecaModel, SymbolTable> compilationResult =
                compileModel(model, extension, CoreVersion.CORE_2_3);

        RILModel transformModel =
                rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);

        printAllMethods(transformModel);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    public void printAllMethods(RILModel transformModel) {
        for(String methodName : transformModel.getMethodNames()) {
            System.out.println(methodName);
            int counter = 0;
            for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
                System.out.println("" + counter++ +":" + instruction);
            }
            System.out.println("...............................................");
        }
    }

    private void printExceptions() {
        Collection<Set<Exception>> exceptions = exceptionContainer.getExceptions().values();
        for (Set<Exception> exceptionCollection: exceptions) {
            for (Exception exception: exceptionCollection) {
                System.out.println(exception);
            }

        }

    }
}