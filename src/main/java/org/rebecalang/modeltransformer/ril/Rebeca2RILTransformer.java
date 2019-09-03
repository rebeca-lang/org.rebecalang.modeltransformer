package org.rebecalang.modeltransformer.ril;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.ril.corerebeca.CoreRebecaModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.timedrebeca.TimedRebecaModelTransformer;

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

		AbstractModelTransformer modelTransformer = null;

		if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
			// TODO

		} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {

			modelTransformer = new TimedRebecaModelTransformer();

			modelTransformer.prepare(null, model.getFirst(), compilerFeatures, transformingFeatures, null, null,
					container);

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
