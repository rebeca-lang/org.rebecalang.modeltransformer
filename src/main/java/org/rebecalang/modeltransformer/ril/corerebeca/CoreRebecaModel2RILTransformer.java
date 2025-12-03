package org.rebecalang.modeltransformer.ril.corerebeca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.CastExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InstanceofExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.NonDetExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecInstantiationPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.AbstractRILModelTransformer;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.FieldDeclarationTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.BinaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.CastExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.DotPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.InstanceofExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.LiteralStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.NonDetExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.PlusSubExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.RebecInstantiationPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator.UnaryExpressionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class CoreRebecaModel2RILTransformer extends AbstractRILModelTransformer  {

	@Autowired
	public CoreRebecaModel2RILTransformer(
			Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}
	
	public void initializeTranslators() {
		
		statementTranslatorContainer.registerTranslator(BlockStatement.class,
				appContext.getBean(BlockStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ReturnStatement.class,
				appContext.getBean(ReturnStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(FieldDeclaration.class,
				appContext.getBean(FieldDeclarationTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(WhileStatement.class,
				appContext.getBean(WhileStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(SwitchStatement.class,
				appContext.getBean(SwitchStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ForStatement.class,
				appContext.getBean(ForStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(BreakStatement.class,
				appContext.getBean(BreakStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ContinueStatement.class,
				appContext.getBean(ContinueStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ConditionalStatement.class,
				appContext.getBean(ConditionalStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));

		expressionTranslatorContainer.registerTranslator(BinaryExpression.class,
				appContext.getBean(BinaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(InstanceofExpression.class,
				appContext.getBean(InstanceofExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(Literal.class, 
				appContext.getBean(LiteralStatementTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(DotPrimary.class,
				appContext.getBean(DotPrimaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(UnaryExpression.class,
				appContext.getBean(UnaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(CastExpression.class,
				appContext.getBean(CastExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(PlusSubExpression.class,
				appContext.getBean(PlusSubExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(NonDetExpression.class,
				appContext.getBean(NonDetExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(RebecInstantiationPrimary.class,
				appContext.getBean(RebecInstantiationPrimaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(TermPrimary.class,
				(TermPrimaryExpressionTranslator)appContext.getBean("CORE_REBECA_TERM_PRIMARY", 
						expressionTranslatorContainer));
		
	}

	@Override
	public RILModel transformModel(
			Pair<RebecaModel, SymbolTable> model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion) {

		RILModel transformedRILModel = new RILModel();
		
		RebecaModel rebecaModel = model.getFirst();
		
		statementTranslatorContainer.setSymbolTable(model.getSecond());
		expressionTranslatorContainer.setSymbolTable(model.getSecond());

		List<ReactiveClassDeclaration> reactiveClassDeclarations = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
			for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
				if(msgsrv.isAbstract())
					continue;
				String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
				ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, msgsrv.getBlock());
				instructions.add(new EndMsgSrvInstructionBean());
				transformedRILModel.addMethod(computedMethodName, instructions);
			}
			for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
				String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
				ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, constructorDeclaration.getBlock());
				instructions.add(new EndMethodInstructionBean());
				transformedRILModel.addMethod(computedMethodName, instructions);
			}
			for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
				if(methodDeclaration.isAbstract())
					continue;
				String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
				ArrayList<InstructionBean> instructions = generateMethodRIL(rcd, computedMethodName, methodDeclaration.getBlock());
				instructions.add(new EndMethodInstructionBean());
				transformedRILModel.addMethod(computedMethodName, instructions);
			}
		}
		
		ArrayList<InstructionBean> mainBlockInstruction = 
				transformMainBlock(rebecaModel);
		mainBlockInstruction.add(new EndMsgSrvInstructionBean());
		transformedRILModel.addMethod("main", mainBlockInstruction);
		
		return transformedRILModel;
	}

	private ArrayList<String> extractKnownrebecsNames(ReactiveClassDeclaration rcd) {
		ArrayList<String> retValue = new ArrayList<String>();
		Type extendsType = rcd.getExtends();
		if(extendsType != null) {
			AbstractTypeSystem typeSystem = extendsType.getTypeSystem();
			try {
				ReactiveClassDeclaration metaData = 
						(ReactiveClassDeclaration) typeSystem.getMetaData(extendsType);
				retValue.addAll(extractKnownrebecsNames(metaData));
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
		for(FieldDeclaration fd : rcd.getKnownRebecs()) {
			for(VariableDeclarator vd : fd.getVariableDeclarators())
				retValue.add(vd.getVariableName());
		}
		return retValue;
	}
	
	private ArrayList<InstructionBean> transformMainBlock(RebecaModel rebecaModel) {
		HashMap<String, ArrayList<String>> knownrebecsNames = 
				new HashMap<String, ArrayList<String>>();
		for(ReactiveClassDeclaration rcd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			knownrebecsNames.put(rcd.getName(), extractKnownrebecsNames(rcd));
		}
		
		BlockStatement blockStatement = new BlockStatement();
		LinkedList<BinaryExpression> setBindingsInstructions = 
				new LinkedList<BinaryExpression>();
		for(MainRebecDefinition mrd : rebecaModel.getRebecaCode().getMainDeclaration().getMainRebecDefinition()) {
			FieldDeclaration fd = createFieldDeclaration(mrd);
			blockStatement.getStatements().add(fd);

			RebecInstantiationPrimary rip = getRebecInstantiationPrimary(fd);

			rip.setCharacter(mrd.getCharacter());
			rip.setLineNumber(mrd.getLineNumber());
			rip.setType(mrd.getType());
			rip.getAnnotations().addAll(mrd.getAnnotations());
			rip.getArguments().addAll(mrd.getArguments());
			for(int cnt = 0; cnt < mrd.getBindings().size(); cnt++) {
				Expression binding = mrd.getBindings().get(cnt);
				Type bindingType = binding.getType();
				
				BinaryExpression be = new BinaryExpression();
				setBindingsInstructions.add(be);
				be.setOperator("=");
				be.setType(bindingType);
				be.setRight(binding);

				DotPrimary left = new DotPrimary();
				be.setLeft(left);
				left.setType(bindingType);
				TermPrimary base = new TermPrimary();
				base.setType(bindingType);
				base.setName(mrd.getName());
				left.setLeft(base);
				TermPrimary variable = new TermPrimary();
				variable.setType(bindingType);
				variable.setName(knownrebecsNames.get(mrd.getType().getTypeName()).get(cnt));
				left.setRight(variable);
			}
		}
		blockStatement.getStatements().addAll(setBindingsInstructions);
		ArrayList<InstructionBean> instructions = generateMethodRIL(null, "main", blockStatement);
		ArrayList<InstructionBean> instantiations = new ArrayList<InstructionBean>();
		instantiations.add(instructions.get(0));
		int cnt = 0;
		while(true) {
			if(cnt >= instructions.size())
				break;
			if(instructions.get(cnt) instanceof RebecInstantiationInstructionBean) {
				instantiations.add(instructions.remove(cnt - 2));
				instantiations.add(instructions.remove(cnt - 2));
				instantiations.add(instructions.remove(cnt - 2));
				instantiations.add(instructions.remove(cnt - 1));
				cnt-=2;
			} else
				cnt++;
		}
		instantiations.addAll(instructions);
		return instantiations;
	}

	private RebecInstantiationPrimary getRebecInstantiationPrimary(FieldDeclaration fd) {
		return (RebecInstantiationPrimary) 
				((OrdinaryVariableInitializer)fd.getVariableDeclarators().get(0).getVariableInitializer()).getValue();
	}

	private FieldDeclaration createFieldDeclaration(MainRebecDefinition mrd) {
		FieldDeclaration fd = new FieldDeclaration();
		fd.setType(mrd.getType());
		VariableDeclarator vd = new VariableDeclarator();
		fd.getVariableDeclarators().add(vd);
		vd.setVariableName(mrd.getName());
		OrdinaryVariableInitializer ovi = new OrdinaryVariableInitializer();
		ovi.setType(mrd.getType());
		vd.setVariableInitializer(ovi);
		RebecInstantiationPrimary rip = new RebecInstantiationPrimary();
		ovi.setValue(rip);
		return fd;
	}

	private ArrayList<InstructionBean> generateMethodRIL(ReactiveClassDeclaration rcd, String computedMethodName, Statement statement) {
		ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
		statementTranslatorContainer.prepare(rcd, computedMethodName);
		statementTranslatorContainer.translate(statement, instructions);
		return instructions;
	}
	
}
