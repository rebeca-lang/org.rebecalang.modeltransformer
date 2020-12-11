package org.rebecalang.modeltransformer;

import java.util.Set;

import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;

public abstract class AbstractStatementTransformer {
	public final static String NEW_LINE = "\r\n";
	protected final static String TAB = "\t";
	protected ExceptionContainer container = new ExceptionContainer();
	protected AbstractExpressionTransformer expressionTransformer;
	protected Set<CompilerFeature> cFeatures;

	public AbstractStatementTransformer(AbstractExpressionTransformer expressionTranslator,
			Set<CompilerFeature> cFeatures) {
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
