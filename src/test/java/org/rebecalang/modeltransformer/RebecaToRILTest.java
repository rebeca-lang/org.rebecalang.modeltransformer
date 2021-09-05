package org.rebecalang.modeltransformer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
public class RebecaToRILTest {
	
	@Autowired
	Rebeca2RILModelTransformer rebeca2RIL;
	
	@Autowired
	RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	private String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modeltransformer/testcase/";

	protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
		return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
	}

	@Test
	public void generateDiningPhilosophers() {
		String modelName = "DiningPhilosophers";
		File model = new File(MODEL_FILES_BASE + modelName + ".rebeca");
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		for(String methodName : transformModel.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : transformModel.getInstructionList(methodName)) {
				System.out.println("" + counter++ +":" + instruction);
			}
			System.out.println("...............................................");
		}
	}

}