package org.rebecalang.modeltransformer.ril;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractRILModelTransformer {
	
	@Autowired
	protected ConfigurableApplicationContext appContext;
	
	protected Rebeca2RILStatementTranslatorContainer statementTranslatorContainer;
	
	protected Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;

	@Autowired
	public AbstractRILModelTransformer(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		this.expressionTranslatorContainer = expressionTranslatorContainer;
		this.statementTranslatorContainer = statementTranslatorContainer;
		this.statementTranslatorContainer.setExpressionTranslatorContainer(expressionTranslatorContainer);
	}

	public abstract RILModel transformModel(
			Pair<RebecaModel,SymbolTable> model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion);
}
