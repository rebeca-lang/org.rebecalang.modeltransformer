package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import static org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator.INVALID_JUMP_LOCATION;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatementGroup;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class SwitchStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		SwitchStatement switchStatement = (SwitchStatement) statement;
		List<SwitchStatementGroup> switchStatementGroups = switchStatement.getSwitchStatementGroups();
		String computedMethodName = StatementTranslatorContainer.getInstance().getComputedMethodName();
		List<JumpIfNotInstructionBean> listOfJumpInstructions = new LinkedList<JumpIfNotInstructionBean>();
		Object valueOfCaseExpression = ExpressionTranslatorContainer.getInstance()
				.translate(switchStatement.getExpression(), instructions);
		Variable tempVariable = AbstractStatementTranslator.getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		for (SwitchStatementGroup ssg : switchStatementGroups) {
			if (ssg.getExpression() == null)
				continue;
			Object ssgStatementTranslated = ExpressionTranslatorContainer.getInstance().translate(ssg.getExpression(),
					instructions);
			AssignmentInstructionBean assignmentInstructionBean = new AssignmentInstructionBean(tempVariable,
					ssgStatementTranslated, valueOfCaseExpression, "!=");
			instructions.add(assignmentInstructionBean);
			JumpIfNotInstructionBean jinib = new JumpIfNotInstructionBean(tempVariable, computedMethodName, INVALID_JUMP_LOCATION);
			listOfJumpInstructions.add(jinib);
			instructions.add(jinib);
		}
		JumpIfNotInstructionBean jumpToDefault = new JumpIfNotInstructionBean(null, computedMethodName, INVALID_JUMP_LOCATION);
		instructions.add(jumpToDefault);
		int counter = 0;
		for (SwitchStatementGroup ssg : switchStatementGroups) {
			if (ssg.getExpression() == null) {
				jumpToDefault.setLineNumber(instructions.size());
			} else {
				listOfJumpInstructions.get(counter++).setLineNumber(instructions.size());
			}
			for (Statement ssgStatement : ssg.getStatements())
				StatementTranslatorContainer.getInstance().translate(ssgStatement, instructions);

		}
		if (jumpToDefault.getLineNumber() == INVALID_JUMP_LOCATION)
			jumpToDefault.setLineNumber(instructions.size());
	}

}
