package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;

public class BreakStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		instructions.add(new PopARInstructionBean(0, true));
		instructions.add(
				new JumpIfNotInstructionBean(null, computedMethodName, JumpIfNotInstructionBean.BREAK_JUMP_INDICATOR));
	}

}
