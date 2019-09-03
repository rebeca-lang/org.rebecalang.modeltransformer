package org.rebecalang.modeltransformer.ril.timedrebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;

public class TimedRebecaTermPrimaryExpressionTranslator extends TermPrimaryExpressionTranslator {
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
			afterResult = ExpressionTranslatorContainer.getInstance().translate(afterExpression, instructions);
		Expression deadlineExpression = parentSuffixPrimary.getDeadlineExpression();
		if (deadlineExpression != null)
			deadlineResult = ExpressionTranslatorContainer.getInstance().translate(deadlineExpression, instructions);
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
		if (!TypesUtilities.getInstance().canTypeCastTo(
				statement.getParentSuffixPrimary().getArguments().get(0).getType(), TypesUtilities.INT_TYPE))
			return false;
		return true;
	}

}
