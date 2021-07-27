package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
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
public class ForStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public ForStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		ForStatement forStatement = (ForStatement) statement;
		instructions.add(new PushARInstructionBean());
		ForInitializer forInitializer = forStatement.getForInitializer();
		statementTranslatorContainer.translate(forInitializer.getFieldDeclaration(), instructions);

		int beginOfForLineNumber = instructions.size();
		String computedMethodName = statementTranslatorContainer.getComputedMethodName();
		Object conditionVariable = expressionTranslatorContainer.translate(forStatement.getCondition(),
				instructions);
		JumpIfNotInstructionBean jumpToEndFor = new JumpIfNotInstructionBean(conditionVariable, computedMethodName, -1);
		instructions.add(jumpToEndFor);

		statementTranslatorContainer.translate(forStatement.getStatement(), instructions);

		int beginOfIncrementStep = instructions.size();
		List<Expression> forIncrementExpressions = forStatement.getForIncrement();
		for (Expression exp : forIncrementExpressions) {
			expressionTranslatorContainer.translate(exp, instructions);
		}
		JumpIfNotInstructionBean jumpToTop = new JumpIfNotInstructionBean(null, computedMethodName,
				beginOfForLineNumber);
		instructions.add(jumpToTop);
		jumpToEndFor.setLineNumber(instructions.size());
		WhileStatementTranslator.fillBreakAndContinueJumpLocations(instructions, beginOfForLineNumber,
				beginOfIncrementStep, instructions.size());
		instructions.add(new PopARInstructionBean());
	}

}
