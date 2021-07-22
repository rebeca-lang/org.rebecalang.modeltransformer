package org.rebecalang.modeltransformer.ril.timedrebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimedRebecaTermPrimaryExpressionTranslator extends TermPrimaryExpressionTranslator {

	@Autowired
	public TimedRebecaTermPrimaryExpressionTranslator(
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	protected CallTimedMsgSrvInstructionBean createMsgSrvCallInstructionBean(Variable baseVariable,
			ArrayList<Object> parameterTempObjects, String computedMethodName, TermPrimary termPrimary,
			ArrayList<InstructionBean> instructions) {
		TimedRebecaParentSuffixPrimary parentSuffixPrimary = (TimedRebecaParentSuffixPrimary) termPrimary
				.getParentSuffixPrimary();
		Expression afterExpression = parentSuffixPrimary.getAfterExpression();
		Object afterResult = 0;
		Object deadlineResult = Integer.MAX_VALUE;
		if (afterExpression != null)
			afterResult = expressionTranslatorContainer.translate(afterExpression, instructions);
		Expression deadlineExpression = parentSuffixPrimary.getDeadlineExpression();
		if (deadlineExpression != null)
			deadlineResult = expressionTranslatorContainer.translate(deadlineExpression, instructions);
		return new CallTimedMsgSrvInstructionBean(baseVariable, computedMethodName, parameterTempObjects, afterResult,
				deadlineResult);

	}

	@Override
	protected boolean isBuiltInMethod(Expression expression) {
		return (super.isBuiltInMethod(expression) || isDelayMethod((TermPrimary) expression));
	}

	private boolean isDelayMethod(TermPrimary statement) {
		if (!statement.getName().equals("delay"))
			return false;
		if (statement.getParentSuffixPrimary() == null)
			return false;
		if (statement.getParentSuffixPrimary().getArguments().size() != 1)
			return false;
		if (!statement.getParentSuffixPrimary().getArguments().get(0).getType().canTypeCastTo(CoreRebecaTypeSystem.INT_TYPE))
			return false;
		return true;
	}

}
