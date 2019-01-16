package org.rebecalang.modeltransformer.maude.timedrebeca;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecInstantiationPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TernaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.AbstractExpressionTransformer;
import org.rebecalang.modeltransformer.StatementTransformingException;
import org.rebecalang.modeltransformer.TransformingContext;
import org.rebecalang.modeltransformer.TransformingFeature;

public class CoreRebecaExpressionTransformer extends AbstractExpressionTransformer {

	public CoreRebecaExpressionTransformer(Set<CompilerFeature> cFeatures,
			Set<TransformingFeature> tFeatures, ExceptionContainer container) {
		super(cFeatures, tFeatures, container);
	}

	public void initialize() {
	}
	
	public String translate(Expression expression,
			ExceptionContainer container) {
		String retValue = "";

		if (expression instanceof TernaryExpression) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"ternary expression.", 
					expression.getLineNumber(), expression.getCharacter()));
		} else if (expression instanceof BinaryExpression) {
			BinaryExpression bExpression = (BinaryExpression) expression;
			String op = bExpression.getOperator();
			if (op.equals("="))
				op = ":=";
			else if (op.equals("=="))
				op = "=";
			else if (op.equals("/"))
				op = "//";
			op = " " + op + " ";
			retValue = "(" + translate(bExpression.getLeft(), container) +
					 " " + op + " " + translate(bExpression.getRight(), container) + ")";
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression uExpression = (UnaryExpression) expression;
			retValue = "( " + uExpression.getOperator() + " " + translate(uExpression.getExpression(), container) + ")";
		} else if (expression instanceof CastExpression) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"cast\" expression.", 
					expression.getLineNumber(), expression.getCharacter()));
		} else if (expression instanceof NonDetExpression) {
			retValue = " ( ";
			for (Expression nonDetChoices : ((NonDetExpression)expression).getChoices()) {
				retValue += " ( " + translate(nonDetChoices, container) + " ) ? ";
			}
			retValue = retValue.substring(0, retValue.length() - 2) + ") ";
		} else if (expression instanceof Literal) {
			Literal lExpression = (Literal) expression;
			retValue = lExpression.getLiteralValue();
			if (retValue.equals("null"))
				retValue = "\"dummy\"";
		} else if (expression instanceof PlusSubExpression) {
			container.addException(new StatementTransformingException("This version of transformer does not supprt " +
					"\"++\" and \"--\" expression.", 
					expression.getLineNumber(), expression.getCharacter()));
		} else if (expression instanceof PrimaryExpression) {
			PrimaryExpression pExpression = (PrimaryExpression) expression;
			retValue = translatePrimaryExpression(pExpression);
		} else {
			container.addException(new StatementTransformingException("Unknown translation rule for expression type " 
					+ expression.getClass(), expression.getLineNumber(), expression.getCharacter()));
		}
		return retValue;
	}

	protected String translatePrimaryExpression(
			PrimaryExpression pExpression) {
		String retValue = "";
		if (pExpression instanceof DotPrimary) {
			DotPrimary dotPrimary = (DotPrimary) pExpression;
			if (!(dotPrimary.getLeft() instanceof TermPrimary) || !(dotPrimary.getRight() instanceof TermPrimary)) {
				container.addException(new StatementTransformingException("This version of transformer does not supprt " +
						"nested record access expression.", 
						pExpression.getLineNumber(), pExpression.getCharacter()));
			} else {
				String args = "";
				for (Expression expression : ((TermPrimary)dotPrimary.getRight()).getParentSuffixPrimary().getArguments()) {
					args += "arg((" + translate(expression, container) + ")) ";
				}
				if (args.equals(""))
					args = "noArg";
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TransformingContext.getInstance().lookupInContext("current-reactive-class");
				retValue = ((TermPrimary) dotPrimary.getLeft()).getName();
				String typeName = TypesUtilities.getTypeName(((TermPrimary) dotPrimary.getLeft()).getType());
				retValue = "(send " + typeName + "--" + ((TermPrimary) dotPrimary.getRight()).getName() +
						" with " + args + " to " + (retValue.equals("self") ? "" : (rcd.getName() + "-")) + ((TermPrimary) dotPrimary.getLeft()).getName() + ")";
			}
		} else if (pExpression instanceof TermPrimary) {
			retValue = translatePrimaryTermExpression((TermPrimary) pExpression);
		} else if (pExpression instanceof RebecInstantiationPrimary) {
			RebecInstantiationPrimary rip = (RebecInstantiationPrimary) pExpression;
			boolean hasMoreVariable = false;
			String args = "";
			try {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(rip.getType());
				if (!rcd.getStatevars().isEmpty()) {
					args += " , ";
					for (FieldDeclaration fd : rcd.getStatevars()) {
						for (VariableDeclarator vd : fd.getVariableDeclarators()) {
							hasMoreVariable = true;
							String typeInit = fd.getType() == TypesUtilities.BOOLEAN_TYPE ? "false" : 
								TypesUtilities.getInstance().canTypeCastTo(fd.getType(), TypesUtilities.INT_TYPE) ? "0" : "\"dummy\"";
							args += "(" + rcd.getName() + "-" + vd.getVariableName() + " |-> " + typeInit + ") " ;
						}
					}
				}
				if (!hasMoreVariable)
					args += "emptyValuation";
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
			args += ",";
			hasMoreVariable = false;
			String typeName = TypesUtilities.getTypeName(rip.getType());
			for (Expression expression : rip.getBindings()) {
				args += " arg(" + translate(expression, container) + ")";
				hasMoreVariable = true;
			}
			for (Expression expression : rip.getArguments()) {
				args += " arg(" + translate(expression, container) + ")";
				hasMoreVariable = true;
			}
			if (!hasMoreVariable)
				args += "noArg";
			retValue = " new (" + typeName + args + ")";
		} else {
			container.addException(new StatementTransformingException("Unknown translation rule for initializer type " 
					+ pExpression.getClass(), pExpression.getLineNumber(), pExpression.getCharacter()));
		}
		return retValue;
	}

	protected String translatePrimaryTermExpression(TermPrimary termPrimary) {
		ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TransformingContext.getInstance().lookupInContext("current-reactive-class");
		String retValue = termPrimary.getName();
		if (!retValue.equals("sender") && !retValue.equals("self")) {
			retValue = rcd.getName() + "-"+ retValue;
		}
		return retValue;
	}

}
