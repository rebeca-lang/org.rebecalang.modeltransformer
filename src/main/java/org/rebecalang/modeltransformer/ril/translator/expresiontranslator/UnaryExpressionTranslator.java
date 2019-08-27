package org.rebecalang.modeltransformer.ril.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;

public class UnaryExpressionTranslator extends AbstractExpressionTranslator {

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		UnaryExpression unaryExpression = (UnaryExpression) expression;
		String operator = unaryExpression.getOperator();

		Object translatedExpression = ExpressionTranslatorContainer.getInstance()
				.translate(unaryExpression.getExpression(), instructions);

		Variable tempVariable = getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		AssignmentInstructionBean assignmentInstruction = null;
			assignmentInstruction = new AssignmentInstructionBean(tempVariable, translatedExpression, null, 
					operator);

		instructions.add(assignmentInstruction);
		return tempVariable;
	}

}
