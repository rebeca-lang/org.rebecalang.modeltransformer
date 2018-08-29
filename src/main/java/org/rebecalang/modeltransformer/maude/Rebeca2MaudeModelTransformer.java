package org.rebecalang.modeltransformer.maude;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.maude.timedrebeca.TimedRebecaModelTransformer;

public class Rebeca2MaudeModelTransformer {
	private static Rebeca2MaudeModelTransformer instance = new Rebeca2MaudeModelTransformer();

	ExceptionContainer container = new ExceptionContainer();

	private Rebeca2MaudeModelTransformer() {

	}

	public static Rebeca2MaudeModelTransformer getInstance() {
		return instance;
	}

	public ExceptionContainer getExceptionContainer() {
		return container;
	}

	public void transformModel(File rebecaFile, File destinationLocation,
			Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> transformingFeatures,
			CommandLine commandLine) {

		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		this.container = rebecaCompiler.getExceptionContainer();

		RebecaModel rebecaModel = rebecaCompiler.compileRebecaFile(rebecaFile,
				compilerFeatures).getFirst();
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		AbstractModelTransformer modelTransformer = null;
		rebecaModel = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures).getFirst();
		if (!container.getExceptions().isEmpty()) {
			return;
		}
		if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)
				&& compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
		} else if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA)) {
			modelTransformer = new TimedRebecaModelTransformer();
			String modelName = rebecaFile.getName().split("\\.")[0];
			
			modelTransformer.prepare(modelName, rebecaModel, compilerFeatures,
					transformingFeatures, commandLine, destinationLocation,
					container);
		} else if (compilerFeatures
				.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
		} else {
		}
		try {
			modelTransformer.transformModel();
		} catch (IOException e) {
			this.container.addException(e);
		}

	}
}
