package org.rebecalang.modeltransformer.ril.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.JumpIfNotInstructionBean;


public class ConditionalStatementTranslator extends AbstractStatementTranslator {

	public static final int INVALID_JUMP_LOCATION = -1;
	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		ConditionalStatement conditionalStatement = (ConditionalStatement) statement;
		Object conditionVariable = ExpressionTranslatorContainer.getInstance().translate(conditionalStatement.getCondition(), instructions);
		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		JumpIfNotInstructionBean jumpToElse = new JumpIfNotInstructionBean(conditionVariable, computedMethodName, INVALID_JUMP_LOCATION);
		instructions.add(jumpToElse);
		StatementTranslatorContainer.getInstance().translate(conditionalStatement.getStatement(), instructions);
		jumpToElse.setLineNumber(instructions.size());
		if(conditionalStatement.getElseStatement() != null) {
			JumpIfNotInstructionBean jumpToEnd = new JumpIfNotInstructionBean(null, computedMethodName, INVALID_JUMP_LOCATION);
			instructions.add(jumpToEnd);
			jumpToElse.setLineNumber(instructions.size());
			StatementTranslatorContainer.getInstance().translate(conditionalStatement.getElseStatement(), instructions);
			jumpToEnd.setLineNumber(instructions.size());
		}
	}

}
