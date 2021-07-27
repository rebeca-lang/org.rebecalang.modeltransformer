package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlusSubExpressionTranslator extends AbstractExpressionTranslator {


	@Autowired
	public PlusSubExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		PlusSubExpression psExpression = (PlusSubExpression) expression;
		Object evaluatedValue = expressionTranslatorContainer.translate(psExpression.getValue(),
				instructions);
		Variable tempVariable = AbstractExpressionTranslator.getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		instructions.add(new AssignmentInstructionBean(tempVariable, evaluatedValue, null, null));
		instructions.add(new AssignmentInstructionBean(evaluatedValue, evaluatedValue, 1,
				psExpression.getOperator().substring(1)));
		return tempVariable;
	}


}
