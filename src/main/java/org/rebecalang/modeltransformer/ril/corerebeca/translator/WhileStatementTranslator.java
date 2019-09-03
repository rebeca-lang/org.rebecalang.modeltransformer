package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import static org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator.INVALID_JUMP_LOCATION;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;

public class WhileStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		int beginOfWhileLineNumber = instructions.size();
		WhileStatement whileStatement = (WhileStatement) statement;

		Object conditionVariable = ExpressionTranslatorContainer.getInstance().translate(whileStatement.getCondition(),
				instructions);
		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		JumpIfNotInstructionBean jumpToEndWhile = new JumpIfNotInstructionBean(conditionVariable, computedMethodName,
				INVALID_JUMP_LOCATION);
		instructions.add(jumpToEndWhile);
		StatementTranslatorContainer.getInstance().translate(whileStatement.getStatement(), instructions);
		JumpIfNotInstructionBean jumpToTop = new JumpIfNotInstructionBean(null, computedMethodName,
				INVALID_JUMP_LOCATION);
		instructions.add(jumpToTop);
		jumpToTop.setLineNumber(beginOfWhileLineNumber);
		jumpToEndWhile.setLineNumber(instructions.size());
		fillBreakAndContinueJumpLocations(instructions, beginOfWhileLineNumber, beginOfWhileLineNumber,
				instructions.size());
	}

	public static void fillBreakAndContinueJumpLocations(ArrayList<InstructionBean> instructions, int beginOfSearch,
			int continueJumpLocation, int breakJumpLocation) {
		int counterOfUnpopedPushes = 0;
		for (int i = beginOfSearch; i < instructions.size(); i++) {
			InstructionBean instructionBean = instructions.get(i);
			if (instructionBean instanceof PushARInstructionBean)
				counterOfUnpopedPushes++;
			else if (instructionBean instanceof PopARInstructionBean) {
				if (!((PopARInstructionBean) instructionBean).isBreakOrContinuePOP())
					counterOfUnpopedPushes--;
			}
			if (!(instructionBean instanceof JumpIfNotInstructionBean))
				continue;
			JumpIfNotInstructionBean jiib = (JumpIfNotInstructionBean) instructionBean;
			if (jiib.getLineNumber() == JumpIfNotInstructionBean.BREAK_JUMP_INDICATOR) {
				jiib.setLineNumber(breakJumpLocation);
				((PopARInstructionBean)instructions.get(i-1)).setNumberOfPops(counterOfUnpopedPushes);
			} else if (jiib.getLineNumber() == JumpIfNotInstructionBean.CONTINUE_JUMP_INDICATOR) {
				jiib.setLineNumber(continueJumpLocation);
				((PopARInstructionBean)instructions.get(i-1)).setNumberOfPops(counterOfUnpopedPushes);
			}
		}


	}

}
