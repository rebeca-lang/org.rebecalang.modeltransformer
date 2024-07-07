package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.AbstractExpressionTranslator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Rebeca2RILExpressionTranslatorContainer {

	Hashtable<Class<? extends Expression>, AbstractExpressionTranslator> translators;
	private String computedMethodName;
	private ReactiveClassDeclaration reactiveClassDeclaration;
	private SymbolTable symbolTable;

	public Rebeca2RILExpressionTranslatorContainer() {
		translators = new Hashtable<Class<? extends Expression>, AbstractExpressionTranslator>();
	}

	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		AbstractExpressionTranslator expressionTranslator = translators.get(expression.getClass());
		Object expressionResult = expressionTranslator.translate(expression, instructions);
		return expressionResult;

	}

	public AbstractExpressionTranslator getTranslator(Class<? extends Expression> clazz) {

		return translators.get(clazz);

	}

	public void registerTranslator(Class<? extends Expression> clazz,
			AbstractExpressionTranslator expressionTranslator) {
		translators.put(clazz, expressionTranslator);
	}

	public void prepare(ReactiveClassDeclaration rcd, String computedMethodName) {
		this.setReactiveClassDeclaration(rcd);
		this.setComputedMethodName(computedMethodName);
	}

	public String getComputedMethodName() {
		return computedMethodName;
	}

	public void setComputedMethodName(String computedMethodName) {
		this.computedMethodName = computedMethodName;
	}

	public ReactiveClassDeclaration getReactiveClassDeclaration() {
		return reactiveClassDeclaration;
	}

	public void setReactiveClassDeclaration(ReactiveClassDeclaration reactiveClassDeclaration) {
		this.reactiveClassDeclaration = reactiveClassDeclaration;
	}
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
}
