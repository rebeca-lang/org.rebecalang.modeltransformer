package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;


public class BlockStatementTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		BlockStatement blockStatement = (BlockStatement) statement;
		instructions.add(new PushARInstructionBean());
		for(Statement insideStatement : blockStatement.getStatements())
			StatementTranslatorContainer.getInstance().translate(insideStatement, instructions);
		instructions.add(new PopARInstructionBean());
	}

}
