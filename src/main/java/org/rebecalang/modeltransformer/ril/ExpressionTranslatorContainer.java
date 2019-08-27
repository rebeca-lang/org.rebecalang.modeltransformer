package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.AbstractExpressionTranslator;

public class ExpressionTranslatorContainer {

	public static void main(String[] args) {
		int x = 0;
		int y = (x--) + (x++);
		System.out.println(y);
	}

	static ExpressionTranslatorContainer instance = new ExpressionTranslatorContainer();

	private ExpressionTranslatorContainer() {
		translators = new Hashtable<Class<? extends Expression>, AbstractExpressionTranslator>();
	}

	public static ExpressionTranslatorContainer getInstance() {
		return instance;
	}

	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		AbstractExpressionTranslator expressionTranslator = translators.get(expression.getClass());
		Object expressionResult = expressionTranslator.translate(expression, instructions);
		return expressionResult;

	}

	public AbstractExpressionTranslator getTranslator(Class<? extends Expression> clazz) {

		return translators.get(clazz);

	}

	Hashtable<Class<? extends Expression>, AbstractExpressionTranslator> translators;
	private String computedMethodName;
	private ReactiveClassDeclaration reactiveClassDeclaration;

	public void registerTranslator(Class<? extends Expression> clazz,
			AbstractExpressionTranslator expressionTranslator) {
		translators.put(clazz, expressionTranslator);
	}

	// public SymbolTable getSymbolTable() {
	// return symbolTable;
	// }

	// public void setSymbolTable(SymbolTable symbolTable) {
	// this.symbolTable = symbolTable;
	// }

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
}
