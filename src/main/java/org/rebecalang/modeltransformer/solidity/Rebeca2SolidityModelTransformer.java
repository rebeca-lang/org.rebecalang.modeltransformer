package org.rebecalang.modeltransformer.solidity;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.ros.timedrebeca.TimedRebecaModelTransformer;
import org.rebecalang.modeltransformer.solidity.corerebeca.CoreRebecaModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("Rebeca2SolidityTransformer")
public class Rebeca2SolidityModelTransformer{

	@Autowired
	private ExceptionContainer container;

	@Autowired
	private RebecaCompiler rebecaCompiler;
	
	@Autowired
	@Qualifier("Rebeca2SolidityTransformer")
	private CoreRebecaModelTransformer modelTransformer;
	
	public ExceptionContainer getExceptionContainer() {
		return container;
	}
	
	public void transformModel(File rebecaFile, File destinationLocation,
			Set<CompilerFeature> compilerFeatures,
			CommandLine commandLine) {


		Pair<RebecaModel, SymbolTable> model = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures);
		//TODO remove this line when compiler is changed to Spring DI style
		this.container.addAll(rebecaCompiler.getExceptionContainer());

		if (!container.exceptionsIsEmpty()) {
			return;
		}
		AbstractModelTransformer modelTransformer = null;
		/*model = rebecaCompiler.compileRebecaFile(rebecaFile, compilerFeatures);
		if (!container.getExceptions().isEmpty()) {
			return;
		}*/
		
		String modelName = rebecaFile.getName().split("\\.")[0];
		
		this.modelTransformer.prepare(modelName, model.getFirst(), compilerFeatures,
				commandLine, destinationLocation, container);
		try {
			this.modelTransformer.transformModel();
		} catch (IOException e) {
			this.container.addException(e);
		}
	}
	
}
