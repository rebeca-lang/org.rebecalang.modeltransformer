package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;

public class NonDetExpressionTranslator extends AbstractExpressionTranslator {

	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		NonDetExpression nonDetExpression = (NonDetExpression) expression;
		NonDetValue nonDetValue = new NonDetValue();
		for(Expression nonDetChoice : nonDetExpression.getChoices()) {
			Object value = ExpressionTranslatorContainer.getInstance().translate(nonDetChoice, instructions);
			nonDetValue.addNonDetValue(value);
		}
		
		return nonDetValue;
	}

}
