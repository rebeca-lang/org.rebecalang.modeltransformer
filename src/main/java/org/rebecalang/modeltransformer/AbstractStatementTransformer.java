package org.rebecalang.modeltransformer;

import java.util.Set;

import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractStatementTransformer {
	public final static String NEW_LINE = "\r\n";
	protected final static String TAB = "\t";
	protected ExceptionContainer container = new ExceptionContainer();
	protected AbstractExpressionTransformer expressionTransformer;
	protected Set<CompilerExtension> cFeatures;

	public AbstractStatementTransformer(AbstractExpressionTransformer expressionTranslator,
			Set<CompilerExtension> cFeatures) {
		this.expressionTransformer = expressionTranslator;
		this.cFeatures = cFeatures;
	}
	
	public AbstractExpressionTransformer getExpressionTranslator() {
		return expressionTransformer;
	}
	
	public ExceptionContainer getExceptionContainer() {
		return container;
	}
	
}
