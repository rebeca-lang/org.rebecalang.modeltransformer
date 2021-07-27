package org.rebecalang.modeltransformer.solidity.corerebeca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Annotation;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.TransformationException;
import org.rebecalang.modeltransformer.solidity.Rebeca2SolidityExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.solidity.Rebeca2SolidityProperties;
import org.rebecalang.modeltransformer.solidity.Rebeca2SolidityStatementTranslatorContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("Rebeca2SolidityTransformer")
public class CoreRebecaModelTransformer {

	private StringBuilder contract;
	
	private Rebeca2SolidityExpressionTranslatorContainer expressionTranslatorContainer;
	private Rebeca2SolidityStatementTranslatorContainer statementTranslatorContainer;
	
	@Autowired
	private CoreRebecaTypeSystem coreRebecaTypeSystem;

	@Autowired
	ExceptionContainer exceptionContainer;
	
	@Autowired
	public CoreRebecaModelTransformer(
			Rebeca2SolidityStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2SolidityExpressionTranslatorContainer expressionTranslatorContainer) {
		this.statementTranslatorContainer = statementTranslatorContainer;
		this.expressionTranslatorContainer = expressionTranslatorContainer;
		contract = new StringBuilder();
	}

	
	private void appendLine(String lineContent) {
		contract.append(lineContent);
		contract.append("\n");
	}
	
	public void transformModel(Pair<RebecaModel, SymbolTable> model, 
			CoreVersion coreVersion, Rebeca2SolidityProperties properties) throws IOException {

		RebecaModel rebecaModel = model.getFirst();
		
		List<ReactiveClassDeclaration> reactiveClassDeclarations = 
				rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		
		ReactiveClassDeclaration contractRC = findContractReactiveClass(reactiveClassDeclarations);
		if(contractRC == null) {
			exceptionContainer.addException(new TransformationException("None of the reactive classes is a contract"));
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
		writeSolidityContractToFile(properties.getModelName(), properties.getDestinationFolder());

	}

	private String transformFormalParameters(List<FormalParameterDeclaration> fpds) {
		String parameters = "";
		for(FormalParameterDeclaration fpd : fpds) {
			parameters += ", " + getTypeName(fpd.getType()) + " " + fpd.getName();
		}
		parameters = parameters.length() == 0 ? "()" : ("(" + parameters.substring(2) + ")");
		return parameters;
	}
	private void writeSolidityContractToFile(String modelName, File destinationFolder) throws FileNotFoundException, IOException {
		String fileName = destinationFolder.getAbsolutePath() + File.separatorChar + modelName;
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
				if(rcd.getExtends() == coreRebecaTypeSystem.getType("Contract")) {
					return rcd;
				}
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String getTypeName(Type type) {
		if (type == CoreRebecaTypeSystem.INT_TYPE)
			return "uint";
		return type.getTypeName();
	}

}
