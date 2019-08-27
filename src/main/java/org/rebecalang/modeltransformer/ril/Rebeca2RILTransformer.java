package org.rebecalang.modeltransformer.ril;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BreakStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ContinueStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ForStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PlusSubExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReturnStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SwitchStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.WhileStatement;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.translator.BlockStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.BreakStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.ConditionalStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.ContinueStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.FieldDeclarationTranslator;
import org.rebecalang.modeltransformer.ril.translator.ForStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.ReturnStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.SwitchStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.WhileStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.BinaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.DotPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.LiteralStatementTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.PlusSubExpressionTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.TermPrimaryExpressionTranslator;
import org.rebecalang.modeltransformer.ril.translator.expresiontranslator.UnaryExpressionTranslator;

public class Rebeca2RILTransformer {

	private Hashtable<String, ArrayList<InstructionBean>> transformedRILModel = new Hashtable<String, ArrayList<InstructionBean>>();
	ExceptionContainer container = new ExceptionContainer();
	private static Rebeca2RILTransformer instance = new Rebeca2RILTransformer();

	public static Rebeca2RILTransformer getInstance() {
		return instance;
	}

	public Hashtable<String, ArrayList<InstructionBean>> getTransformedRILModel() {
		return transformedRILModel;
	}

	public void transformModel(File rebecaFile, File destinationLocation, Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> transformingFeatures, CommandLine commandLine) {
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		this.container = new ExceptionContainer();
		this.container = rebecaCompiler.getExceptionContainer();
		Pair<RebecaModel, SymbolTable> model = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures);
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		transformModel(model, compilerFeatures, transformingFeatures);
	}

	public void transformModel(Pair<RebecaModel, SymbolTable> model, Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> transformingFeatures) {

		StatementTranslatorContainer.getInstance().setSymbolTable(model.getSecond());
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
		AbstractModelTransformer modelTransformer = null;

		if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
			// TODO

		} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {

			// TODO

		} else {
			modelTransformer = new CoreRebecaModelTransformer();

			modelTransformer.prepare(null, model.getFirst(), compilerFeatures, transformingFeatures, null, null,
					container);
		}
		try {
			modelTransformer.transformModel();
			transformedRILModel = ((AbstractRILModelTransformer) modelTransformer).getTransformedModelList();
			for(String methodName : transformedRILModel.keySet()) {
				System.out.println(methodName);
				int counter = 0;
				for(InstructionBean instruction : transformedRILModel.get(methodName)) {
					System.out.println("" + counter++ +":" + instruction);
				}
				System.out.println("...............................................");
			}
		} catch (IOException e) {
			this.container.addException(e);
		}

	}
}
