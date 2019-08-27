package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.translator.AbstractStatementTranslator;

public class StatementTranslatorContainer {

	
	private SymbolTable symbolTable;
	static StatementTranslatorContainer instance = new StatementTranslatorContainer();
	
	private StatementTranslatorContainer() {
		translators = new Hashtable<Class<? extends Statement>, AbstractStatementTranslator>();
	}

	public static StatementTranslatorContainer getInstance() {
		return instance;
	}

	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		if(statement instanceof Expression)
			ExpressionTranslatorContainer.getInstance().translate((Expression) statement, instructions);
		else
			translators.get(statement.getClass()).translate(statement , instructions);
		
	}

	public AbstractStatementTranslator getTranslator(Class<? extends Statement> clazz) {
		
		return translators.get(clazz);
		
	}

	Hashtable<Class<? extends Statement>, AbstractStatementTranslator> translators;
	private String computedMethodName;
	private ReactiveClassDeclaration reactiveClassDeclaration;
	
	public void registerTranslator(Class<? extends Statement> clazz,
			AbstractStatementTranslator statementTranslator) {
		translators.put(clazz, statementTranslator);
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
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
		ExpressionTranslatorContainer.getInstance().setReactiveClassDeclaration(reactiveClassDeclaration);
	}
}
