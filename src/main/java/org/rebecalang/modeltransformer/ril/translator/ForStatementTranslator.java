package org.rebecalang.modeltransformer.ril.translator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.PushARInstructionBean;

public class ForStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		ForStatement forStatement = (ForStatement) statement;
		instructions.add(new PushARInstructionBean());
		ForInitializer forInitializer = forStatement.getForInitializer();
		StatementTranslatorContainer.getInstance().translate(forInitializer.getFieldDeclaration(), instructions);

		int beginOfForLineNumber = instructions.size();
		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		Object conditionVariable = ExpressionTranslatorContainer.getInstance().translate(forStatement.getCondition(),
				instructions);
		JumpIfNotInstructionBean jumpToEndFor = new JumpIfNotInstructionBean(conditionVariable, computedMethodName, -1);
		instructions.add(jumpToEndFor);

		StatementTranslatorContainer.getInstance().translate(forStatement.getStatement(), instructions);

		int beginOfIncrementStep = instructions.size();
		List<Expression> forIncrementExpressions = forStatement.getForIncrement();
		for (Expression exp : forIncrementExpressions) {
			ExpressionTranslatorContainer.getInstance().translate(exp, instructions);
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
