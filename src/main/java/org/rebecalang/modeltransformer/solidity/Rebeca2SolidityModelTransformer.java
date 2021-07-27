package org.rebecalang.modeltransformer.solidity;

import java.io.IOException;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.TransformationException;
import org.rebecalang.modeltransformer.solidity.corerebeca.CoreRebecaModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Rebeca2SolidityModelTransformer{

	@Autowired
	private CoreRebecaModelTransformer modelTransformer;
	
	@Autowired
	ExceptionContainer exceptionContainer;
	
	public void transformModel(Pair<RebecaModel, SymbolTable> model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion, Rebeca2SolidityProperties properties) {
		if(!extension.isEmpty())
			exceptionContainer.addException(
					new TransformationException("This version supports CoreRebeca to Solidity transformation."));

		try {
			modelTransformer.transformModel(model, coreVersion, properties);
		} catch (IOException e) {
			if(exceptionContainer.exceptionsIsEmpty())
				exceptionContainer.addException(e);
		}
	}
	
}
