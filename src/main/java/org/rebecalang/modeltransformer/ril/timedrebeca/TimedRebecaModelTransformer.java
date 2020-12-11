package org.rebecalang.modeltransformer.ril.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.CoreRebecaModelTransformer;
import org.rebecalang.modeltransformer.ril.timedrebeca.translator.TimedRebecaTermPrimaryExpressionTranslator;

public class TimedRebecaModelTransformer extends CoreRebecaModelTransformer {

	@Override
	protected void initializeStatementTranslators() {
		super.initializeStatementTranslators();
		ExpressionTranslatorContainer.getInstance().registerTranslator(TermPrimary.class,
				new TimedRebecaTermPrimaryExpressionTranslator());
		
	}
}
