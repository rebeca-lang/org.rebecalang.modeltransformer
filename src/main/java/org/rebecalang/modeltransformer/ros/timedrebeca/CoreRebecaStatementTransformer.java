package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.util.List;
import java.util.Set;


import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryPrimitiveType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatementGroup;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.modeltransformer.AbstractExpressionTransformer;
import org.rebecalang.modeltransformer.AbstractStatementTransformer;
import org.rebecalang.modeltransformer.StatementTransformingException;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSTypesUtilities;


public class CoreRebecaStatementTransformer extends AbstractStatementTransformer {

	public CoreRebecaStatementTransformer(AbstractExpressionTransformer expressionTranslator,
			Set<CompilerFeature> cFeatures, Set<TransformingFeature> tFeatures) {
		super(expressionTranslator, cFeatures, tFeatures);
	}
	
	public String transformMsgsrvBody(MethodDeclaration methodDeclaration) throws CodeCompilationException {
		expressionTransformer.initialize();
		String retValue = resolveBlockStatement(methodDeclaration.getBlock());
		return retValue;
	}

	protected String resolveStatement(Statement statement) {
		if (statement instanceof BlockStatement) {
			return resolveBlockStatement((BlockStatement) statement);
		}
		if (statement instanceof ConditionalStatement) {
			return resolveIfStatement((ConditionalStatement) statement);
		}
		if (statement instanceof WhileStatement) {
			return resolveWhileStatement((WhileStatement) statement);
		}
		if (statement instanceof ForStatement) {
			return resolveForStatement((ForStatement)statement);
		}
		if (statement instanceof ReturnStatement) {
			return resolveReturnStatement((ReturnStatement) statement);
		}
		if (statement instanceof BreakStatement) {
			return resolveBreakStatement((BreakStatement)statement);
		}
		if (statement instanceof ContinueStatement) {
			return resolveContinueStatement((ContinueStatement) statement);
		}
		if (statement instanceof SwitchStatement) {
			return resolveSwitchStatement((SwitchStatement) statement);
		}
		if (statement instanceof FieldDeclaration) {
			return resolveFieldDeclaration((FieldDeclaration) statement);
		}
		if (statement instanceof Expression) {
			return resolveExpression((Expression) statement);
		}
		
		
		container.addException(new StatementTransformingException("Unknown translation rule for type " + 
				statement.getClass(), statement.getLineNumber(), statement.getCharacter()));
		return "";
	}
	
	
	protected String resolveBlockStatement(BlockStatement blockStatement) {
		String retValue = "{";
		for (Statement statement : blockStatement.getStatements()) {
			retValue += resolveStatement(statement) + ";" + NEW_LINE;
		}
		retValue += "}" + NEW_LINE;
		return retValue;
	}
	
	

	protected String resolveReturnStatement(ReturnStatement statement) {
		String retValue = "";
		retValue += "return ";
		Expression expression = statement.getExpression();
		retValue += resolveExpression(expression);
		return retValue;
	}

	protected String resolveWhileStatement(WhileStatement statement) {
		String retValue = "while(";
		retValue += resolveExpression(statement.getCondition()) + ") {" + NEW_LINE;
		if(statement.getStatement() != null)
			retValue += resolveStatement(statement.getStatement());
		retValue += NEW_LINE + "}";
		return retValue;
	}

	protected String resolveIfStatement(ConditionalStatement statement) {
		String retValue = "if (";
		retValue += resolveExpression(statement.getCondition()) + ") {";
		if (statement.getStatement() != null)
			retValue += resolveStatement(statement.getStatement()) + ";";
		retValue += "}";
		if (statement.getElseStatement() != null) {
			retValue += NEW_LINE + "else {" + NEW_LINE;
			retValue += resolveStatement(statement.getElseStatement()) + ";";
			retValue += "}";
		}
		return retValue;
	}

	protected String resolveExpression(Expression expression) {
		return expressionTransformer.translate(expression, container);
	}

	private String resolveFieldDeclaration(FieldDeclaration statement) {
		String retValue = "";
		for(VariableDeclarator var : ((FieldDeclaration)statement).getVariableDeclarators()) {
			retValue += resolveVariableDeclaration(statement, var);
		}
		return retValue;
	}

	public String resolveVariableDeclaration(FieldDeclaration fd, VariableDeclarator vd) {
		String retValue = "";
		VariableInitializer variableInitializer = vd.getVariableInitializer();
		if(fd.getType() instanceof ArrayType) {
			retValue += Rebeca2ROSTypesUtilities.getCppArrayDefinition(fd, vd);
			if(variableInitializer != null && variableInitializer instanceof ArrayVariableInitializer)
				retValue += " = " + getArrayInitialValues(((ArrayVariableInitializer)variableInitializer));
		}
		
		if(fd.getType() instanceof OrdinaryPrimitiveType) {
			retValue += Rebeca2ROSTypesUtilities.getCorrespondingCppType(fd.getType()) +
			" " + vd.getVariableName();
			if(variableInitializer != null && variableInitializer instanceof OrdinaryVariableInitializer) {
				String value = resolveExpression(((OrdinaryVariableInitializer)variableInitializer).getValue());
				retValue += " = " + value;
			}
		}
		retValue += ";";
		return retValue;
	}
	
	public String getArrayInitialValues(ArrayVariableInitializer arrayVariableInitializer) {
		String retValue = "{";
		List<VariableInitializer> varInits = arrayVariableInitializer.getValues();
		for(VariableInitializer varInit: varInits) {
			if(varInit instanceof OrdinaryVariableInitializer) {
				retValue += resolveExpression(((OrdinaryVariableInitializer)(varInit)).getValue());
			}
			if(varInit instanceof ArrayVariableInitializer) {
				retValue += getArrayInitialValues(((ArrayVariableInitializer)varInit));
			}
			retValue += ",";
		}
		if(varInits.size() > 0) {
			retValue = retValue.substring(0, retValue.length() - 1);
		}
		retValue += "}";
		return retValue;
	}
	
	protected String resolveSwitchStatement(SwitchStatement statement) {
		String retValue = "";
		String switchExpression = resolveExpression(((SwitchStatement)statement).getExpression());
		retValue += "switch" + "(" + switchExpression + ")" + "{" + NEW_LINE;
		for(SwitchStatementGroup thisCase : ((SwitchStatement)statement).getSwitchStatementGroups()) {
			retValue += "case " + resolveExpression(thisCase.getExpression()) + " : ";
			retValue += "{" + NEW_LINE;
			for(Statement stmnt : thisCase.getStatements()) {
				retValue += resolveStatement(stmnt) + "; ";
			}
			retValue += "}" + NEW_LINE;
		}
		retValue += "}" + NEW_LINE;  
		return retValue;
	}

	protected String resolveContinueStatement(ContinueStatement statement) {
		return "continue;";
	}

	protected String resolveBreakStatement(BreakStatement statement) {
		return "break;";
	}

	protected String resolveForStatement(ForStatement statement) {
		String retValue = "";
		retValue += "for(";
		ForInitializer forInitializer = ((ForStatement)statement).getForInitializer();
		retValue += resolveFieldDeclaration(forInitializer.getFieldDeclaration());
		retValue += resolveExpression(((ForStatement)statement).getCondition());
		retValue += "; ";
		for(Expression increment : ((ForStatement)statement).getForIncrement()) {
			retValue += resolveExpression(increment);
		}
		retValue += ")" + "{" + NEW_LINE;
		retValue += resolveStatement(((ForStatement)statement).getStatement()) + ";";
		retValue += "}" + NEW_LINE;
		return retValue;
	}

	
}