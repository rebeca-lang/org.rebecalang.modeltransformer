package org.rebecalang.modeltransformer.ril.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.PopARInstructionBean;

public class ContinueStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		instructions.add(new PopARInstructionBean(0, true));
		instructions
				.add(new JumpIfNotInstructionBean(null, computedMethodName, JumpIfNotInstructionBean.CONTINUE_JUMP_INDICATOR));
	}

}
