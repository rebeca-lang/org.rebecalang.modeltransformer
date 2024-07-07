package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NonDetExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public NonDetExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		NonDetExpression nonDetExpression = (NonDetExpression) expression;
		NonDetValue nonDetValue = new NonDetValue();
		for(Expression nonDetChoice : nonDetExpression.getChoices()) {
			Object value = expressionTranslatorContainer.translate(nonDetChoice, instructions);
			nonDetValue.addNonDetValue(value);
		}
		
		return nonDetValue;
	}

}
