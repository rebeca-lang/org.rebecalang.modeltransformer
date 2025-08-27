package org.rebecalang.modeltransformer.ril;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.TransformationException;
import org.rebecalang.modeltransformer.ril.corerebeca.CoreRebecaModel2RILTransformer;
import org.rebecalang.modeltransformer.ril.timedrebeca.TimedRebecaModel2RILTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Rebeca2RILModelTransformer {


	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
	RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	@Qualifier("TIMED_REBECA")
	TimedRebecaModel2RILTransformer timedRebecaModelTransformer;
	
	@Autowired
	@Qualifier("CORE_REBECA")
	CoreRebecaModel2RILTransformer coreRebecaModelTransformer;
	
	@Autowired
	protected GenericApplicationContext appContext;

	
	public RILModel transformModel(Pair<RebecaModel, SymbolTable> model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion) {

		coreRebecaModelTransformer.initializeTranslators();
		timedRebecaModelTransformer.initializeTranslators();
		
		AbstractRILModelTransformer modelTransformer = null;

		if (extension.contains(CompilerExtension.PROBABILISTIC_REBECA)) {
			exceptionContainer.addException(new 
					TransformationException("There is no transformer for Probabilistic models to RIL."));
			return null;

		} else if (extension.contains(CompilerExtension.TIMED_REBECA)) {
			appContext.registerAlias("timedRebecaTypeSystem", "typeSystem");
			modelTransformer = timedRebecaModelTransformer;
		} else {
			appContext.registerAlias("coreRebecaTypeSystem", "typeSystem");
			modelTransformer = coreRebecaModelTransformer;
		}
		
		return modelTransformer.transformModel(model, extension, coreVersion);
	}
}
