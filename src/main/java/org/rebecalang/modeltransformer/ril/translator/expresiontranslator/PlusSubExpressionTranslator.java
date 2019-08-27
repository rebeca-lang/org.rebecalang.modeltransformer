package org.rebecalang.modeltransformer.ril.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;
import org.rebecalang.modeltransformer.ril.translator.AbstractStatementTranslator;

public class PlusSubExpressionTranslator extends AbstractExpressionTranslator {


	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		PlusSubExpression psExpression = (PlusSubExpression) expression;
		Object evaluatedValue = ExpressionTranslatorContainer.getInstance().translate(psExpression.getValue(),
				instructions);
		Variable tempVariable = AbstractStatementTranslator.getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		instructions.add(new AssignmentInstructionBean(tempVariable, evaluatedValue, null, null));
		instructions.add(new AssignmentInstructionBean(evaluatedValue, evaluatedValue, 1,
				psExpression.getOperator().substring(1)));
		return tempVariable;
	}


}
