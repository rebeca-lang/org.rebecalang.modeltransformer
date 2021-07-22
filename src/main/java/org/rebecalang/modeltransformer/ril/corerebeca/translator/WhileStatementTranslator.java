package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import static org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator.INVALID_JUMP_LOCATION;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WhileStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public WhileStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		int beginOfWhileLineNumber = instructions.size();
		WhileStatement whileStatement = (WhileStatement) statement;

		Object conditionVariable = expressionTranslatorContainer.translate(whileStatement.getCondition(),
				instructions);
		String computedMethodName = statementTranslatorContainer.getComputedMethodName();
		JumpIfNotInstructionBean jumpToEndWhile = new JumpIfNotInstructionBean(conditionVariable, computedMethodName,
				INVALID_JUMP_LOCATION);
		instructions.add(jumpToEndWhile);
		statementTranslatorContainer.translate(whileStatement.getStatement(), instructions);
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
