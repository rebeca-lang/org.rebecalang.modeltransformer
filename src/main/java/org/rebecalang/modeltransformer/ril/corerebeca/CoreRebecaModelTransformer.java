package org.rebecalang.modeltransformer.ril.corerebeca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.modeltransformer.ril.AbstractRILModelTransformer;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.FieldDeclarationTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.BinaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.DotPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.LiteralStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.PlusSubExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.UnaryExpressionTranslator;

public class CoreRebecaModelTransformer extends AbstractRILModelTransformer  {

	public CoreRebecaModelTransformer() {
		initializeStatementTranslators();
	}
	
	protected void initializeStatementTranslators() {

		ExpressionTranslatorContainer.getInstance().registerTranslator(BinaryExpression.class,
				new BinaryExpressionTranslator());
		ExpressionTranslatorContainer.getInstance().registerTranslator(TermPrimary.class,
				new TermPrimaryExpressionTranslator());
		ExpressionTranslatorContainer.getInstance().registerTranslator(Literal.class, 
				new LiteralStatementTranslator());
		ExpressionTranslatorContainer.getInstance().registerTranslator(DotPrimary.class,
				new DotPrimaryExpressionTranslator());
		ExpressionTranslatorContainer.getInstance().registerTranslator(UnaryExpression.class,
				new UnaryExpressionTranslator());
		ExpressionTranslatorContainer.getInstance().registerTranslator(PlusSubExpression.class,
				new PlusSubExpressionTranslator());
		
		StatementTranslatorContainer.getInstance().registerTranslator(BlockStatement.class,
				new BlockStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(ReturnStatement.class,
				new ReturnStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(FieldDeclaration.class,
				new FieldDeclarationTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(WhileStatement.class,
				new WhileStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(SwitchStatement.class,
				new SwitchStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(ForStatement.class,
				new ForStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(BreakStatement.class,
				new BreakStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(ContinueStatement.class,
				new ContinueStatementTranslator());
		StatementTranslatorContainer.getInstance().registerTranslator(ConditionalStatement.class,
				new ConditionalStatementTranslator());
	}

	@Override
	public void transformModel() throws IOException {

		List<ReactiveClassDeclaration> reactiveClassDeclarations = this.rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
			for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(msgsrv.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(constructorDeclaration.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(methodDeclaration.getBlock(), instructions);
				instructions.add(new EndMethodInstructionBean());
			}
		}
	}

}
