/*                          In the name of Allah                         */
/*                           The Best will come                          */

package org.rebecalang.modeltransformer.maude.timedrebeca;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.AbstractExpressionTransformer;
import org.rebecalang.modeltransformer.AbstractStatementTransformer;
import org.rebecalang.modeltransformer.StatementTransformingException;
import org.rebecalang.modeltransformer.TransformingFeature;

public class CoreRebecaStatementTransformer extends AbstractStatementTransformer {

	public CoreRebecaStatementTransformer(
			AbstractExpressionTransformer expressionTransformer,
			Set<CompilerFeature> cFeatures, Set<TransformingFeature> tFeatures) {
		super(expressionTransformer, cFeatures, tFeatures);
	}
	
	public String getTypeName(Type type) {
		return TypesUtilities.getTypeName(type);
	}

	public String transformMsgsrvBody(MethodDeclaration methodDeclaration) throws CodeCompilationException {
		expressionTransformer.initialize();
		String retValue = resolveBlockStatement(methodDeclaration.getBlock());
		return retValue;
	}
	
	public String transformSynchMethodBody(MethodDeclaration methodDeclaration) throws CodeCompilationException {
		container.addException(new StatementTransformingException("This version of transformer does not supprt " +
				"synch method declaration.", 
				methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
		return "";
	}
	
	public String transformConstructorBody(MethodDeclaration methodDeclaration) throws CodeCompilationException {
		expressionTransformer.initialize();
		String retValue = resolveBlockStatement(methodDeclaration.getBlock());
		return retValue;
	}
	
	public String resolveBlockStatement(BlockStatement blockStatement) throws CodeCompilationException {
		String retValue = "(" + NEW_LINE;
		for (Statement statement : blockStatement.getStatements())
			retValue += resolveStatement(statement) + " ; " + NEW_LINE;
		retValue += NEW_LINE + "noStatements )";
		return retValue;
	}
	
	public String resolveStatement(Statement statement) throws CodeCompilationException {
		if (statement instanceof BlockStatement) {
			return resolveBlockStatement((BlockStatement) statement);
		}
		if (statement instanceof ConditionalStatement) {
			return resolveIfStatement((ConditionalStatement) statement);
		}
		if (statement instanceof WhileStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"while\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
		}
		if (statement instanceof ForStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"for\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
		}
		if (statement instanceof ReturnStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"return\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
		}
		if (statement instanceof BreakStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"break\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
		}
		if (statement instanceof ContinueStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"continue\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
		}
		if (statement instanceof SwitchStatement) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"switch\" statement.", 
					statement.getLineNumber(), statement.getCharacter()));
			return "";
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

	private String resolveFieldDeclaration(FieldDeclaration statement) {
//		String definitionPart = "";
//		Type type = statement.getType();
//		if (TypesUtilities.getInstance().canTypeCastTo(type, TypesUtilities.INT_TYPE))
//			definitionPart = "defI";
		return "unauthorized-local-variable-decleration";
	}

	protected String resolveIfStatement(ConditionalStatement statement) throws CodeCompilationException {
		String retValue = "( if (";
		retValue += expressionTransformer.translate(
				statement.getCondition(), container) + ") then (" + NEW_LINE;
		if (statement.getStatement() != null)
			retValue += resolveStatement(statement.getStatement());
		retValue += ")" + NEW_LINE + "else (" + NEW_LINE;
		if (statement.getElseStatement() != null)
			retValue += resolveStatement(statement.getElseStatement());
		else
			retValue += " noStatements ";
		retValue +=  NEW_LINE + ") )";
		return retValue;
	}

	public String  resolveExpression(Expression expression) {
		return expressionTransformer.translate(expression, container);
	}

}