package org.rebecalang.modeltransformer.ril.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;

public class BinaryExpressionTranslator extends AbstractExpressionTranslator {


	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		BinaryExpression binaryExpression = (BinaryExpression) expression;
		String operator = binaryExpression.getOperator();
		Object leftSide = ExpressionTranslatorContainer.getInstance().translate(binaryExpression.getLeft(), instructions);
		Object rightSide = ExpressionTranslatorContainer.getInstance().translate(binaryExpression.getRight(), instructions);
		if (!operator.equals("==") && !operator.equals("!=") && operator.endsWith("=")) {
			AssignmentInstructionBean assignmentInstruction;
			if (operator.equals("=")) {
				assignmentInstruction = new AssignmentInstructionBean(leftSide, rightSide, null, null);
			} else {
				assignmentInstruction = new AssignmentInstructionBean(leftSide,
						leftSide, rightSide, String.valueOf(operator.charAt(0)));
			}
			instructions.add(assignmentInstruction);				
		} else {
			Variable tempVariable = getTempVariable();
			instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
			AssignmentInstructionBean assignmentInstruction = new AssignmentInstructionBean(tempVariable, 
					leftSide, rightSide, operator);
			instructions.add(assignmentInstruction);
			return tempVariable;
		}
		
		return null;
	}

}
