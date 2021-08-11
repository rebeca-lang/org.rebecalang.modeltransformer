package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.SymbolTableException;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("CORE_REBECA_TERM_PRIMARY")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TermPrimaryExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public TermPrimaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Autowired
	@Qualifier("CORE_REBECA")
	CoreRebecaTypeSystem coreRebecaTypeSystem;

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		ReactiveClassDeclaration reactiveClassDeclaration = expressionTranslatorContainer
				.getReactiveClassDeclaration();
		Type baseType = null;
		try {
			baseType = coreRebecaTypeSystem.getType(reactiveClassDeclaration.getName());
		} catch (CodeCompilationException e) {
			e.printStackTrace();
		}
		Variable base = null;
		if (!isBuiltInMethod(expression))
			base = new Variable("self");

		return translate(baseType, base, (TermPrimary) expression, instructions);
	}

	protected boolean isBuiltInMethod(Expression expression) {
		return isPOWMethod((TermPrimary) expression) || isSQRTMethod((TermPrimary) expression)
				|| isAssertionMethod((TermPrimary) expression) || isGetAllActorsMethod((TermPrimary) expression);
	}

	public Object translate(Type baseType, Variable baseVariable, TermPrimary termPrimary,
			ArrayList<InstructionBean> instructions) {
		if (termPrimary.getParentSuffixPrimary() == null)
			return (new Variable(termPrimary.getName()));

		List<Expression> arguments = termPrimary.getParentSuffixPrimary().getArguments();
		ArrayList<Object> parameterTempObjects = new ArrayList<Object>();
		ArrayList<Type> argumentsType = new ArrayList<Type>();
		for (Expression argument : arguments) {
			parameterTempObjects.add(expressionTranslatorContainer.translate(argument, instructions));
			argumentsType.add(argument.getType());
		}

		SymbolTable symbolTable = expressionTranslatorContainer.getSymbolTable();

		MethodInSymbolTableSpecifier castableMethodSpecification = getMethodFromSymbolTable(baseType, termPrimary,
				argumentsType, symbolTable);

		String computedMethodName;
		if (isBuiltInMethod(termPrimary))
			computedMethodName = RILUtilities.computeMethodName(castableMethodSpecification);
		else
			computedMethodName = RILUtilities.computeMethodName(castableMethodSpecification.getRebecType(), castableMethodSpecification);

		if (termPrimary.getType() == CoreRebecaTypeSystem.MSGSRV_TYPE) {
			instructions.add(createMsgSrvCallInstructionBean(baseVariable, parameterTempObjects, computedMethodName, termPrimary, instructions));
			return null;
		}

		Variable tempVariable = null;
		if (termPrimary.getType() != CoreRebecaTypeSystem.VOID_TYPE) {
			tempVariable = AbstractExpressionTranslator.getTempVariable();
			instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		}

		if (termPrimary.getLabel() == CoreRebecaLabelUtility.BUILT_IN_METHOD) {
			instructions.add(new ExternalMethodCallInstructionBean(baseVariable, computedMethodName,
					parameterTempObjects, tempVariable));
		} else {
			MethodCallInstructionBean methodCallInstructionBean = new MethodCallInstructionBean(baseVariable,
					computedMethodName);
			methodCallInstructionBean.setParameters(parameterTempObjects);
			methodCallInstructionBean.setFunctionCallResult(tempVariable);
			methodCallInstructionBean.setParametersNames(castableMethodSpecification.getArgumentsNames());
			instructions.add(methodCallInstructionBean);
		}

		return tempVariable;

	}

	protected CallMsgSrvInstructionBean createMsgSrvCallInstructionBean(Variable baseVariable,
			ArrayList<Object> parameterTempObjects, String computedMethodName,
			TermPrimary termPrimary, ArrayList<InstructionBean> instructions) {
		return new CallMsgSrvInstructionBean(baseVariable, computedMethodName, parameterTempObjects);
	}

	private MethodInSymbolTableSpecifier getMethodFromSymbolTable(Type baseType, TermPrimary termPrimary,
			ArrayList<Type> argumentsType, SymbolTable symbolTable) {

		MethodInSymbolTableSpecifier methodInSymbolTableSpecifier = null;
		Type curType = baseType;

		while(true) {
			try {
				methodInSymbolTableSpecifier = symbolTable.getCastableMethodSpecification(curType, termPrimary.getName(), argumentsType);
				return methodInSymbolTableSpecifier;
			}
			catch(SymbolTableException ste) {
				try {
					ReactiveClassDeclaration metaData = (ReactiveClassDeclaration) curType.getTypeSystem().getMetaData(curType);
					if (metaData.getExtends() == null) {
						try {
							return symbolTable.getCastableMethodSpecification(CoreRebecaTypeSystem.NO_TYPE, termPrimary.getName(),
							argumentsType);
						} catch (SymbolTableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return null;
						}
					}
					else {
						curType = metaData.getExtends();
					}
				} catch (CodeCompilationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isAssertionMethod(TermPrimary statement) {
		if (!statement.getName().equals("assertion"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		int size = statement.getParentSuffixPrimary().getArguments().size();
		if (size != 1 && size != 2)
			return false;
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType().canTypeCastTo(CoreRebecaTypeSystem.BOOLEAN_TYPE))
			return false;
		if (size == 2)
			if (!statement.getParentSuffixPrimary().getArguments().get(1).getType().canTypeCastTo(CoreRebecaTypeSystem.STRING_TYPE))
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
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType().canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE))
			return false;
		if (!statement.getParentSuffixPrimary().getArguments().get(1).getType().canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE))
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
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType().canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE))
			return false;
		return true;
	}

}
