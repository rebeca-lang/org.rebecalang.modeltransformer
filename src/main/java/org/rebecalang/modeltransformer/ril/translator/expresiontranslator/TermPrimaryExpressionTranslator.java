package org.rebecalang.modeltransformer.ril.translator.expresiontranslator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.SymbolTableException;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;
import org.rebecalang.modeltransformer.ril.translator.AbstractStatementTranslator;

public class TermPrimaryExpressionTranslator extends AbstractExpressionTranslator {

//	private static final String TEMP_PREFIX = "$TEMP";

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		ReactiveClassDeclaration reactiveClassDeclaration = ExpressionTranslatorContainer.getInstance()
				.getReactiveClassDeclaration();
		Type baseType = null;
		try {
			baseType = TypesUtilities.getInstance().getType(reactiveClassDeclaration.getName());
		} catch (CodeCompilationException e) {
			e.printStackTrace();
		}
		Variable base = null;
		if (!isPOWMethod((TermPrimary) expression) && !isSQRTMethod((TermPrimary) expression)
				&& !isAssertionMethod((TermPrimary) expression) && !isGetAllActorsMethod((TermPrimary) expression))
			base = new Variable("self");

		return translate(baseType, base, (TermPrimary) expression, instructions);
	}

	public Object translate(Type baseType, Variable baseVariable, TermPrimary termPrimary,
			ArrayList<InstructionBean> instructions) {
		if (termPrimary.getParentSuffixPrimary() == null)
			return (new Variable(termPrimary.getName()));

		List<Expression> arguments = termPrimary.getParentSuffixPrimary().getArguments();
		ArrayList<Object> parameterTempObjects = new ArrayList<Object>();
		ArrayList<Type> argumentsType = new ArrayList<Type>();
		for (Expression argument : arguments) {
			parameterTempObjects.add(ExpressionTranslatorContainer.getInstance().translate(argument, instructions));
			argumentsType.add(argument.getType());
		}

		SymbolTable symbolTable = StatementTranslatorContainer.getInstance().getSymbolTable();

		MethodInSymbolTableSpecifier castableMethodSpecification = getMethodFromSymbolTable(baseType, termPrimary,
				argumentsType, symbolTable);

		
		String computedMethodName;
		if(isPOWMethod(termPrimary) || isSQRTMethod(termPrimary) || isAssertionMethod(termPrimary)
				|| isGetAllActorsMethod(termPrimary))
			computedMethodName = RILUtilities.computeMethodName(castableMethodSpecification);
		else 
			computedMethodName = RILUtilities.computeMethodName(baseType, castableMethodSpecification);

		if (termPrimary.getType() == TypesUtilities.MSGSRV_TYPE) {
			instructions.add(new CallMsgSrvInstructionBean(baseVariable, computedMethodName, parameterTempObjects));
			return null;
		}

		Variable tempVariable = null;
		if(termPrimary.getType() != TypesUtilities.VOID_TYPE) {
			tempVariable = AbstractStatementTranslator.getTempVariable();
			instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		}

		if (termPrimary.getLabel() == CoreRebecaLabelUtility.BUILT_IN_METHOD) {
			instructions.add(new ExternalMethodCallInstructionBean(baseVariable, computedMethodName, parameterTempObjects, tempVariable));
		} else {
			MethodCallInstructionBean methodCallInstructionBean = new MethodCallInstructionBean(baseVariable, computedMethodName);
			methodCallInstructionBean.setParameters(parameterTempObjects);
			methodCallInstructionBean.setFunctionCallResult(tempVariable);
			methodCallInstructionBean.setParametersNames(castableMethodSpecification.getArgumentsNames());
			instructions.add(methodCallInstructionBean);
		}

//		instructions.add(new PushARInstructionBean());
//
//		evaluateParameters(instructions, parameterTempObjects, castableMethodSpecification);
//
//		instructions.add(new MethodCallInstructionBean(computedMethodName));

//		Variable tempVariable = AbstractStatementTranslator.getTempVariable();
//		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
//		instructions.add(new AssignmentInstructionBean(tempVariable, new Variable(RETURN_VALUE), null, null));
		
		return tempVariable;

	}

//	private void evaluateParameters(ArrayList<InstructionBean> instructions, ArrayList<Object> parameterTempObjects,
//			MethodInSymbolTableSpecifier castableMethodSpecification) {
//		List<String> argumentsNames = castableMethodSpecification.getArgumentsNames();
//		for (int i = 0; i < argumentsNames.size(); i++) {
//			Object param = parameterTempObjects.get(i);
//			if (param instanceof Variable) {
//				Variable variableParameter = (Variable) param;
//				if (!variableParameter.getVarName().startsWith(TEMP_PREFIX)) {
//					Variable tempVariable = AbstractStatementTranslator.getTempVariable();
//					instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
//					instructions.add(new AssignmentInstructionBean(tempVariable, variableParameter, null, null));
//					argumentsNames.set(i, tempVariable.getVarName());
//				}
//			}
//		}
//		for (int i = 0; i < argumentsNames.size(); i++) {
//			Object argumentValue = parameterTempObjects.get(i);
//			String argumentName = argumentsNames.get(i);
//			instructions.add(new DeclarationInstructionBean(argumentName));
//			instructions.add(new AssignmentInstructionBean(new Variable(argumentName), argumentValue, null, null));
//		}
//	}

	private MethodInSymbolTableSpecifier getMethodFromSymbolTable(Type baseType, TermPrimary termPrimary,
			ArrayList<Type> argumentsType, SymbolTable symbolTable) {
		try {
			return symbolTable.getCastableMethodSpecification(baseType, termPrimary.getName(), argumentsType);
		} catch (SymbolTableException e) {
			try {
				return symbolTable.getCastableMethodSpecification(TypesUtilities.NO_TYPE, termPrimary.getName(),
						argumentsType);
			} catch (SymbolTableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	private boolean isAssertionMethod(TermPrimary statement) {
		if (!statement.getName().equals("assertion"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		int size = statement.getParentSuffixPrimary().getArguments().size();
		if (size != 1 && size != 2)
			return false;
		if (!TypesUtilities.getInstance().canTypeCastTo(
				statement.getParentSuffixPrimary().getArguments().get(0).getType(), TypesUtilities.BOOLEAN_TYPE))
			return false;
		if (size == 2)
			if (!TypesUtilities.getInstance().canTypeCastTo(
					statement.getParentSuffixPrimary().getArguments().get(1).getType(), TypesUtilities.STRING_TYPE))
				return false;
		return true;
	}

	private boolean isGetAllActorsMethod(TermPrimary statement) {
		if (!statement.getName().equals("getAllActors"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 0)
			return false;
		return true;
	}

	private boolean isPOWMethod(TermPrimary statement) {
		if (!statement.getName().equals("pow"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 2)
			return false;
		if (!TypesUtilities.getInstance().canTypeCastTo(
				statement.getParentSuffixPrimary().getArguments().get(0).getType(), TypesUtilities.DOUBLE_TYPE))
			return false;
		if (!TypesUtilities.getInstance().canTypeCastTo(
				statement.getParentSuffixPrimary().getArguments().get(1).getType(), TypesUtilities.DOUBLE_TYPE))
			return false;
		return true;
	}

	private boolean isSQRTMethod(TermPrimary statement) {
		if (!statement.getName().equals("sqrt"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 1)
			return false;
		if (!TypesUtilities.getInstance().canTypeCastTo(
				statement.getParentSuffixPrimary().getArguments().get(0).getType(), TypesUtilities.DOUBLE_TYPE))
			return false;
		return true;
	}

}
