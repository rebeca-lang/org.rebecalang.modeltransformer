package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modeltransformer.AbstractExpressionTransformer;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.TransformingFeature;
import org.rebecalang.modeltransformer.ros.packageCreator.IncludeDirectoryCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.LaunchDirectoryCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.ROSPackageCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.SrcDirectoryCreator;


public class TimedRebecaModelTransformer extends AbstractModelTransformer{
	
	@Override
	public void prepare(String modelName, RebecaModel rebecaModel,
			Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> transformingFeatures,
			CommandLine commandLine, File destinationLocation,
			ExceptionContainer container) {
		super.prepare(modelName, rebecaModel, compilerFeatures,
				transformingFeatures, commandLine, destinationLocation,
				container); 
	}
	
	@Override
	public void transformModel() throws IOException {
		
		/* configuration files */
		ConfigFilesCreator configFileCreator = new ConfigFilesCreator(modelName, rebecaModel);
		String packageXmlFileContent = configFileCreator.getPackageXmlFileContent();
		String cmakeListFileContent = configFileCreator.getCmakeListFileContent();
		ROSPackageCreator rosPackageCreator = new ROSPackageCreator(destinationLocation, modelName, container);
		rosPackageCreator.addFile("CMakeLists.txt", cmakeListFileContent);
		rosPackageCreator.addFile("package.xml", packageXmlFileContent);
		
		/*nodes files */
		for (ReactiveClassDeclaration rc: rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			AbstractExpressionTransformer expressionTransformer = null;
			expressionTransformer = new CoreRebecaExpressionTransformer(compilerFeatures, transformingFeatures, container, modelName, rc, rebecaModel);

			if(rc.getAnnotations().isEmpty()){
				ReactiveClassTransformer reactiveClassTransformer =
						new ReactiveClassTransformer(rebecaModel, rc, modelName, expressionTransformer, compilerFeatures, transformingFeatures);
				reactiveClassTransformer.transformReactiveClass();
				
				String headerFileContent = reactiveClassTransformer.getHeaderFileContent();
				IncludeDirectoryCreator includeDirCreator = new IncludeDirectoryCreator(destinationLocation, modelName, container);
				includeDirCreator.addFile(rc.getName() + ".h", headerFileContent);
				
				String cppFileContent = reactiveClassTransformer.getCppFileContent();
				SrcDirectoryCreator srcDirCreator = new SrcDirectoryCreator(destinationLocation, modelName, container);
				srcDirCreator.addFile(rc.getName() + ".cpp", cppFileContent);
				
				reactiveClassTransformer.createMsgFiles(destinationLocation, container);
			}
			else {
				ReactiveClassTransformer reactiveClassTransformer =
						new ReactiveClassTransformer(rebecaModel, rc, modelName, expressionTransformer, compilerFeatures, transformingFeatures);
				reactiveClassTransformer.transformReactiveClass();
				reactiveClassTransformer.createMsgFiles(destinationLocation, container);

				
			}
		}
		
		/* launch file */
		AbstractExpressionTransformer expressionTransformer = null;
		expressionTransformer = new CoreRebecaExpressionTransformer(compilerFeatures, transformingFeatures, container, modelName, new ReactiveClassDeclaration(), rebecaModel);
		MainTransformer mainTransformer = new MainTransformer(modelName, rebecaModel, expressionTransformer,
								compilerFeatures, transformingFeatures, container);
		String launchFileContent = mainTransformer.getLaunchFileContent();
		LaunchDirectoryCreator launchDirCreator = new LaunchDirectoryCreator(destinationLocation, modelName, container);
		launchDirCreator.addFile(modelName + ".launch", launchFileContent);
		
	}

	
}