package org.rebecalang.modeltransformer;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractExpressionTransformer {
	
	protected final static String NEW_LINE = "\r\n";
	protected final static String TAB = "\t";

	protected Set<CompilerExtension> cFeatures; 
	protected ExceptionContainer container;
	
	public AbstractExpressionTransformer(Set<CompilerExtension> cFeatures, 
			ExceptionContainer container) {
		this.cFeatures = cFeatures;
		this.container = container;
	}
	public abstract String translate(Expression expression,
			ExceptionContainer container);

	public abstract void initialize();
}
