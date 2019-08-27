package org.rebecalang.modeltransformer.ril.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.rilinstructions.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;

public class ReturnStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		ReturnStatement returnStatement = (ReturnStatement) statement;
		Object translatedReturnValue = ExpressionTranslatorContainer.getInstance().translate(returnStatement.getExpression(), instructions);
		instructions.add(new AssignmentInstructionBean(new Variable(RETURN_VALUE), translatedReturnValue, null, null));
	}
}