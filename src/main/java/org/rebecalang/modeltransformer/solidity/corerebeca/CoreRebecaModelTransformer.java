package org.rebecalang.modeltransformer.solidity.corerebeca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Annotation;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.TransformingException;
import org.rebecalang.modeltransformer.ril.AbstractRILModelTransformer;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.FieldDeclarationTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.BinaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.DotPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.LiteralStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.NonDetExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.PlusSubExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.UnaryExpressionTranslator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("Rebeca2SolidityTransformer")
public class CoreRebecaModelTransformer extends AbstractRILModelTransformer  {

	private StringBuilder contract;
	
	public CoreRebecaModelTransformer() {
//		initializeStatementTranslators();
		contract = new StringBuilder();
	}
//	
//	protected void initializeStatementTranslators() {
//
//		ExpressionTranslatorContainer.getInstance().registerTranslator(BinaryExpression.class,
//				new BinaryExpressionTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(TermPrimary.class,
//				new TermPrimaryExpressionTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(Literal.class, 
//				new LiteralStatementTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(DotPrimary.class,
//				new DotPrimaryExpressionTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(UnaryExpression.class,
//				new UnaryExpressionTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(PlusSubExpression.class,
//				new PlusSubExpressionTranslator());
//		ExpressionTranslatorContainer.getInstance().registerTranslator(NonDetExpression.class,
//				new NonDetExpressionTranslator());
//		
//		StatementTranslatorContainer.getInstance().registerTranslator(BlockStatement.class,
//				new BlockStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(ReturnStatement.class,
//				new ReturnStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(FieldDeclaration.class,
//				new FieldDeclarationTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(WhileStatement.class,
//				new WhileStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(SwitchStatement.class,
//				new SwitchStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(ForStatement.class,
//				new ForStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(BreakStatement.class,
//				new BreakStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(ContinueStatement.class,
//				new ContinueStatementTranslator());
//		StatementTranslatorContainer.getInstance().registerTranslator(ConditionalStatement.class,
//				new ConditionalStatementTranslator());
//	}

	private void appendLine(String lineContent) {
		contract.append(lineContent);
		contract.append("\n");
	}
	
	@Override
	public void transformModel() throws IOException {

		List<ReactiveClassDeclaration> reactiveClassDeclarations = 
				this.rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		
		ReactiveClassDeclaration contractRC = findContractReactiveClass(reactiveClassDeclarations);
		if(contractRC == null) {
			container.addException(new TransformingException("None of the reactive classes is a contract"));
		}
		
		appendLine("contract " + contractRC.getName() + " {");
		
		appendConstantVariables(rebecaModel.getRebecaCode().getEnvironmentVariables());
		appendStateVariables(contractRC.getStatevars());
		
		
		for (MsgsrvDeclaration msgsrv : contractRC.getMsgsrvs()) {
			boolean payable = isPayable(msgsrv);
			String parameters = transformFormalParameters(msgsrv.getFormalParameters());
			appendLine("function " + msgsrv.getName() + parameters + (payable? " payable" : "") + " {" );
			appendLine("}");
		}

//		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
//			for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
//				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
//				String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
//				transformedModelList.put(computedMethodName, instructions);
//				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
//				StatementTranslatorContainer.getInstance().translate(msgsrv.getBlock(), instructions);
//				instructions.add(new EndMsgSrvInstructionBean());
//			}
//			for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
//				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
//				String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
//				transformedModelList.put(computedMethodName, instructions);
//				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
//				StatementTranslatorContainer.getInstance().translate(constructorDeclaration.getBlock(), instructions);
//				instructions.add(new EndMsgSrvInstructionBean());
//			}
//			for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
//				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
//				String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
//				transformedModelList.put(computedMethodName, instructions);
//				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
//				StatementTranslatorContainer.getInstance().translate(methodDeclaration.getBlock(), instructions);
//				instructions.add(new EndMethodInstructionBean());
//			}
//		}
		
		appendLine("}");
		writeSolidityContractToFile();

	}

	private String transformFormalParameters(List<FormalParameterDeclaration> fpds) {
		String parameters = "";
		for(FormalParameterDeclaration fpd : fpds) {
			parameters += ", " + getTypeName(fpd.getType()) + " " + fpd.getName();
		}
		parameters = parameters.length() == 0 ? "()" : ("(" + parameters.substring(2) + ")");
		return parameters;
	}
	private void writeSolidityContractToFile() throws FileNotFoundException, IOException {
		String fileName = destinationLocation.getAbsolutePath() + File.separatorChar + modelName;
		RandomAccessFile ras = new RandomAccessFile(fileName, "rw");
		ras.setLength(0);
		ras.writeBytes(contract.toString());
		ras.close();
	}
	
	private boolean isPayable(MethodDeclaration methodDeclaration) {
		for(Annotation annotation : methodDeclaration.getAnnotations()) {
			if(annotation.getIdentifier().equals("payable"))
				return true;
		}
		return false;
	}
	private void appendVariableList(List<FieldDeclaration> fields, String modifier) {
		for(FieldDeclaration fd : fields) {
			for(VariableDeclarator vd : fd.getVariableDeclarators()) {
				appendLine(getTypeName(fd.getType())
						+ " " + (modifier == null ? "" : (modifier + " ")) + vd.getVariableName() + ";");
			}
		}
	}

	private void appendStateVariables(List<FieldDeclaration> fields) {
		appendVariableList(fields, null);
	}

	private void appendConstantVariables(List<FieldDeclaration> fields) {
		appendVariableList(fields, "public constant");
	}
	
	private ReactiveClassDeclaration findContractReactiveClass(
			List<ReactiveClassDeclaration> reactiveClassDeclarations) {
		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
			if(rcd.getExtends() == null)
				continue;
			try {
				if(rcd.getExtends() == TypesUtilities.getInstance().getType("Contract")) {
					return rcd;
				}
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String getTypeName(Type type) {
		if (type == TypesUtilities.INT_TYPE)
			return "uint";
		return TypesUtilities.getTypeName(type);
	}

}
