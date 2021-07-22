package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConditionalStatementTranslator extends AbstractStatementTranslator {

	public static final int INVALID_JUMP_LOCATION = -1;
	
	@Autowired
	public ConditionalStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		ConditionalStatement conditionalStatement = (ConditionalStatement) statement;
		Object conditionVariable = expressionTranslatorContainer.translate(conditionalStatement.getCondition(), instructions);
		String computedMethodName = statementTranslatorContainer.getComputedMethodName();
		JumpIfNotInstructionBean jumpToElse = new JumpIfNotInstructionBean(conditionVariable, computedMethodName, INVALID_JUMP_LOCATION);
		instructions.add(jumpToElse);
		statementTranslatorContainer.translate(conditionalStatement.getStatement(), instructions);
		jumpToElse.setLineNumber(instructions.size());
		if(conditionalStatement.getElseStatement() != null) {
			JumpIfNotInstructionBean jumpToEnd = new JumpIfNotInstructionBean(null, computedMethodName, INVALID_JUMP_LOCATION);
			instructions.add(jumpToEnd);
			jumpToElse.setLineNumber(instructions.size());
			statementTranslatorContainer.translate(conditionalStatement.getElseStatement(), instructions);
			jumpToEnd.setLineNumber(instructions.size());
		}
	}

}
