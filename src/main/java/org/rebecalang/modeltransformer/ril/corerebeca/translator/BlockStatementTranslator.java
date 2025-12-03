package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlockStatementTranslator extends AbstractStatementTranslator {

	@Autowired
	public BlockStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {
		BlockStatement blockStatement = (BlockStatement) statement;
		instructions.add(new PushARInstructionBean());
		for(Statement insideStatement : blockStatement.getStatements()) {
			if(insideStatement.getClass() == Statement.class)
				continue;
			statementTranslatorContainer.translate(insideStatement, instructions);
		}
		instructions.add(new PopARInstructionBean());
	}

}
