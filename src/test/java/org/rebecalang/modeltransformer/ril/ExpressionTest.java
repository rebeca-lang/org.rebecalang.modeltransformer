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
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ReturnInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
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
	public void returnTest() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test (2) {
					knownrebecs{Test t;}
					Test(int a){}
					msgsrv m1() {
						return;
					}
					int f1() {
						int a = 10;
						return a + 6;
						int b = 4;
					}
				}
				main{}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_2);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		ArrayList<InstructionBean> instructionList = transformModel.getInstructionList("Test.f1");
		InstructionBean instructionBean = instructionList.get(5);
		
		Assertions.assertEquals(ReturnInstructionBean.class, instructionBean.getClass());
		ReturnInstructionBean rib = (ReturnInstructionBean) instructionBean;
		Assertions.assertEquals("$TEMP_EXP$17", ((Variable)rib.getReturnValue()).getVarName());
	}

	
	@Test
	public void fieldDeclarationTest() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test (2) {
					knownrebecs{Test t;}
					Test(int a){}
					
					msgsrv a(Test r) {
						new Test(r):(1);
						new Test(r):(2).a();
						Test t = new Test(r):(3 + 4);
						Test t2 = t;
					}
					void a(){}
				}
				main{}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_2);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		ArrayList<InstructionBean> instructionList = transformModel.getInstructionList("Test.a$Test");
		InstructionBean instructionBean = instructionList.get(4);
		
		Assertions.assertEquals(MethodCallInstructionBean.class, instructionBean.getClass());
		MethodCallInstructionBean methodCIBean = 
				(MethodCallInstructionBean) instructionBean;
		Assertions.assertEquals("Test.Test$int", methodCIBean.getMethodName());
		Assertions.assertEquals("$TEMP_EXP$0", methodCIBean.getBase().getVarName());
	}

	
	@Test
	public void MethodCall() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					msgsrv a() {
						self.b(1);
						b(2);
						int t;
						t = b(2+3);
					}
					int b(int a) {
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
		InstructionBean instructionBean = instructionList.get(2);
		
		Assertions.assertEquals(MethodCallInstructionBean.class, instructionBean.getClass());
		MethodCallInstructionBean methodCIBean = 
				(MethodCallInstructionBean) instructionBean;
		Assertions.assertEquals("Test1.b$int", methodCIBean.getMethodName());
		Assertions.assertEquals("self", methodCIBean.getBase().getVarName());
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
	
	@Test
	public void mainBlock() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					knownrebecs{Test1 t;}
					Test1(int r){}
				}
				main{
					Test1 t1(t2):(2+3);
					Test1 t2(t1):(4);
				}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
		ArrayList<InstructionBean> instructionList = transformModel.getInstructionList("main");
		
		InstructionBean firstInstance = instructionList.get(4);
		Assertions.assertEquals(RebecInstantiationInstructionBean.class, 
				firstInstance.getClass());
		
		InstructionBean secondInstance = instructionList.get(10);
		Assertions.assertEquals(RebecInstantiationInstructionBean.class, 
				secondInstance.getClass());
	}

	@Test
	public void complexDotExpression() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					knownrebecs{Test1 t;}
					msgsrv m1() {
						t.m1();
					}
				}
				main{
					Test1 t1(t2):();
					Test1 t2(t1):();
				}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		
	}

	@Test
	public void VariableDeclarationWithInitializationExpression() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					knownrebecs{Test1 t;}
					msgsrv m1() {
						int a = 4 + 5;
					}
				}
				main{
				}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		ArrayList<InstructionBean> instructionList = 
				transformModel.getInstructionList("Test1.m1");
		Assertions.assertEquals(7, instructionList.size());
		
	}
	
	@Test
	public void NondetExpression() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					knownrebecs{Test1 t;}
					msgsrv m1() {
						int a = ?(4, 2+3);
					}
				}
				main{
				}
				""";
		File model = FileUtils.createTempFile(rebecaModel);
		
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		Pair<RebecaModel, SymbolTable> compilationResult = 
				compileModel(model, extension, CoreVersion.CORE_2_3);
		
		RILModel transformModel = 
				rebeca2RIL.transformModel(compilationResult, extension, CoreVersion.CORE_2_3);
		ArrayList<InstructionBean> instructionList = 
				transformModel.getInstructionList("Test1.m1");
		Assertions.assertEquals(9, instructionList.size());
	}
	
	@Test
	public void testArrayAccess() throws IOException {
		
		String rebecaModel = 
				"""
				reactiveclass Test1 (2) {
					statevars {int[4] b;}
					msgsrv a() {
						b[1 + b[0]] = 2 * 7;
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
		
		AssignmentInstructionBean aib = (AssignmentInstructionBean) instructionList.get(2);
		Assertions.assertEquals(AssignmentInstructionBean.class, 
				aib.getClass());
		Assertions.assertEquals(1, ((Variable)aib.getSecondOperand()).getIndeces().size());
	}
	
	@Test
	public void testEnviroment() throws IOException {
		
		String rebecaModel = 
				"""
				env int[2] a = {20, 3};
				reactiveclass Test1 (2) {
					msgsrv a() {
						int b = a[1];
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
		
		AssignmentInstructionBean aib = (AssignmentInstructionBean) instructionList.get(2);
		Assertions.assertEquals(AssignmentInstructionBean.class, 
				aib.getClass());
	}
}
