package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.SymbolTableException;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecInstantiationPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RebecInstantiationPrimaryExpressionTranslator extends AbstractExpressionTranslator {
	@Autowired
	public RebecInstantiationPrimaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Autowired
	protected GenericApplicationContext appContext;

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		RebecInstantiationPrimary rip = (RebecInstantiationPrimary) expression;

		Variable tempVariable = getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName(), rip.getType()));

		ArrayList<InstructionBean> tempInstructions = new ArrayList<InstructionBean>();
		Map<String, Object> bindings = extractBindings(rip, tempInstructions);
		
		for(Entry<String, Object> entry : bindings.entrySet()) {
			Variable var = new Variable(tempVariable, entry.getKey());
			AssignmentInstructionBean aib = new AssignmentInstructionBean(var, entry.getValue(), null, null);
			tempInstructions.add(aib);
		}
		
		Pair<MethodInSymbolTableSpecifier, Map<String, Object>> specs = 
				extractConsructorSpecification(rip, tempInstructions);

		RebecInstantiationInstructionBean aiib = new RebecInstantiationInstructionBean();
		aiib.setBindings(bindings);
		aiib.setType(rip.getType());
		aiib.setResultTarget(tempVariable);
		instructions.add(aiib);

		instructions.addAll(tempInstructions);
		String computedMethodName = RILUtilities.computeMethodName(rip.getType(), specs.getFirst());
		MethodCallInstructionBean mcib = new MethodCallInstructionBean(tempVariable, computedMethodName, specs.getSecond());
		instructions.add(mcib);
		
		return tempVariable;
	}

	private Map<String, Object> extractBindings(RebecInstantiationPrimary rip, ArrayList<InstructionBean> tempInstructions) {
		Map<String, Object> bindings = new TreeMap<String, Object>();
		if(!rip.getBindings().isEmpty()) {
			List<Object> bindingValues = new ArrayList<Object>();
			for (Expression argument : rip.getBindings()) {
				bindingValues.add(expressionTranslatorContainer.translate(
						argument, tempInstructions));
			}
			try {
				AbstractTypeSystem typeSystem = (AbstractTypeSystem) appContext.getBean("typeSystem");
				ReactiveClassDeclaration metaData = 
						(ReactiveClassDeclaration) typeSystem.getMetaData(rip.getType());
				int cnt = 0;
				for(FieldDeclaration knownrebecsFD : metaData.getKnownRebecs()) {
					for(VariableDeclarator knownrebecDeclaration : knownrebecsFD.getVariableDeclarators()) {
						bindings.put(knownrebecDeclaration.getVariableName(), bindingValues.get(cnt++));
					}
				}
			} catch (CodeCompilationException e) {
				e.printStackTrace();
				assert false;
			}	
		}
		return bindings;
	}
	
	public Pair<MethodInSymbolTableSpecifier, Map<String, Object>> extractConsructorSpecification(RebecInstantiationPrimary rip, ArrayList<InstructionBean> tempInstructions) {
		SymbolTable symbolTable = expressionTranslatorContainer.getSymbolTable();
		Map<String, Object> arguments = new TreeMap<String, Object>();
		ArrayList<Object> argumentsValues = new ArrayList<Object>();
		ArrayList<Type> argumentsType = new ArrayList<Type>();
		for (Expression argument : rip.getArguments()) {
			argumentsValues.add(expressionTranslatorContainer.translate(
					argument, tempInstructions));
			argumentsType.add(argument.getType());
		}
		String typeName = rip.getType().getTypeName();
		MethodInSymbolTableSpecifier castableMethodSpecification = null;
		try {
			castableMethodSpecification = 
					symbolTable.getCastableMethodSpecification(rip.getType(),
					typeName, argumentsType);
			List<String> argumentsNames = castableMethodSpecification.getArgumentsNames();
			
			int cnt = 0;
			for(Object value : argumentsValues) {
				arguments.put(argumentsNames.get(cnt++), value);
			}
		} catch (SymbolTableException e) {
			e.printStackTrace();
			assert false;
		}
		return new Pair<MethodInSymbolTableSpecifier, Map<String, Object>> 
			(castableMethodSpecification, arguments);
	}

}
