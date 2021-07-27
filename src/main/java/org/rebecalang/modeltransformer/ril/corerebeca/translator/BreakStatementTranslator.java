package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BreakStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public BreakStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		String computedMethodName = statementTranslatorContainer.getComputedMethodName();
		instructions.add(new PopARInstructionBean(0, true));
		instructions.add(
				new JumpIfNotInstructionBean(null, computedMethodName, JumpIfNotInstructionBean.BREAK_JUMP_INDICATOR));
	}

}
