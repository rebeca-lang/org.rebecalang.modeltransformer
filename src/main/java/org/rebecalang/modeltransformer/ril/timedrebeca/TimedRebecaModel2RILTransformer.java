package org.rebecalang.modeltransformer.ril.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.CoreRebecaModel2RILTransformer;
import org.rebecalang.modeltransformer.ril.timedrebeca.translator.TimedRebecaTermPrimaryExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("TIMED_REBECA")
public class TimedRebecaModel2RILTransformer extends CoreRebecaModel2RILTransformer {

	@Autowired
	public TimedRebecaModel2RILTransformer(
			Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void initializeTranslators() {
		super.initializeTranslators();
		expressionTranslatorContainer.registerTranslator(TermPrimary.class,
				appContext.getBean(TimedRebecaTermPrimaryExpressionTranslator.class, expressionTranslatorContainer));

	}
}
