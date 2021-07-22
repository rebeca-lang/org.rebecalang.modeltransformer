package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractStatementTranslator {
	

	protected Rebeca2RILStatementTranslatorContainer statementTranslatorContainer;
	protected Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;


	@Autowired
	public AbstractStatementTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		this.statementTranslatorContainer = statementTranslatorContainer;
		this.expressionTranslatorContainer = expressionTranslatorContainer;
	}



	public abstract void translate(Statement statement, ArrayList<InstructionBean> instructions);
}
