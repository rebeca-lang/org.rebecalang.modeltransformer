package org.rebecalang.modeltransformer.ril;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.rebecalang.compiler.utils.FileUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelTransformerConfig.class}) 
@SpringJUnitConfig
public class ExpressionTest {
	@Autowired
	Rebeca2RILModelTransformer rebeca2RIL;
	
	@Autowired
	RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
		return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
	}

	@Test
	public void MsgsrvCall() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					msgsrv a() {
						self.a();
					}
				}
				main{}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		ArrayList<InstructionBean> instructionList = transformModel.getInstructionList("Test1.a");
		InstructionBean instructionBean = instructionList.get(1);
		
		Assertions.assertEquals(MsgsrvCallInstructionBean.class, instructionBean.getClass());
		MsgsrvCallInstructionBean msgsrvCIBean = 
				(MsgsrvCallInstructionBean) instructionBean;
		Assertions.assertEquals("Test1.a", msgsrvCIBean.getMethodName());
		Assertions.assertEquals("self", msgsrvCIBean.getBase().getVarName());
	}
	
	@Test
	public void Expression() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					int f(){}
					msgsrv a() {
						int x;
						x = f() + 2 * 7;
					}
				}
				main{}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		ArrayList<InstructionBean> instructionList = transformModel.getInstructionList("Test1.a");
		
		Assertions.assertEquals(DeclarationInstructionBean.class, 
				instructionList.get(1).getClass());
		Assertions.assertEquals(DeclarationInstructionBean.class, 
				instructionList.get(2).getClass());
		Assertions.assertEquals(MethodCallInstructionBean.class, 
				instructionList.get(3).getClass());
		Assertions.assertEquals(DeclarationInstructionBean.class, 
				instructionList.get(4).getClass());
		Assertions.assertEquals(AssignmentInstructionBean.class, 
				instructionList.get(5).getClass());
		Assertions.assertEquals(DeclarationInstructionBean.class, 
				instructionList.get(6).getClass());
		Assertions.assertEquals(AssignmentInstructionBean.class, 
				instructionList.get(7).getClass());
		Assertions.assertEquals(AssignmentInstructionBean.class, 
				instructionList.get(8).getClass());
		
	}
}
