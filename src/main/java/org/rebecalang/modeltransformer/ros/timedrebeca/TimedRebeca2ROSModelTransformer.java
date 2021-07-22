package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.io.File;
import java.io.IOException;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSProperties;
import org.rebecalang.modeltransformer.ros.packageCreator.IncludeDirectoryCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.LaunchDirectoryCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.ROSPackageCreator;
import org.rebecalang.modeltransformer.ros.packageCreator.SrcDirectoryCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TimedRebeca2ROSModelTransformer {
	
	@Autowired
	ReactiveClassTransformer reactiveClassTransformer;
	
	@Autowired
	IncludeDirectoryCreator includeDirCreator;
	
	@Autowired
	SrcDirectoryCreator srcDirCreator;
	
	@Autowired
	TimedRebeca2ROSExpressionTransformer expressionTransformer;
	
	@Autowired
	MainTransformer mainTransformer;

	@Autowired
	LaunchDirectoryCreator launchDirCreator;
	
	@Autowired
	ROSPackageCreator rosPackageCreator;
	
	public void transformModel(Pair<RebecaModel, SymbolTable> model, 
			CoreVersion coreVersion, Rebeca2ROSProperties properties) throws IOException {
		
		RebecaModel rebecaModel = model.getFirst();
		File destinationFolder = properties.getDestinationFolder();
		String modelName = properties.getModelName();
		
		/* configuration files */
		ConfigFilesCreator configFileCreator = new ConfigFilesCreator(modelName, rebecaModel);
		String packageXmlFileContent = configFileCreator.getPackageXmlFileContent();
		String cmakeListFileContent = configFileCreator.getCmakeListFileContent();
		rosPackageCreator.createDirectory(destinationFolder, modelName);
		rosPackageCreator.addFile(destinationFolder, modelName, "CMakeLists.txt", cmakeListFileContent);
		rosPackageCreator.addFile(destinationFolder, modelName, "package.xml", packageXmlFileContent);
		
		/*nodes files */
		for (ReactiveClassDeclaration rc: rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			
			if(rc.getAnnotations().isEmpty()){
				reactiveClassTransformer.prepare(rc);
				reactiveClassTransformer.transformReactiveClass(rebecaModel, rc, modelName);
				
				String headerFileContent = reactiveClassTransformer.getHeaderFileContent(rebecaModel, rc, modelName);
				includeDirCreator.createDirectory(destinationFolder, modelName);
				includeDirCreator.addFile(destinationFolder, modelName, rc.getName() + ".h", headerFileContent);
				
				String cppFileContent = reactiveClassTransformer.getCppFileContent(rc, modelName);
				srcDirCreator.createDirectory(destinationFolder, modelName);
				srcDirCreator.addFile(destinationFolder, modelName, rc.getName() + ".cpp", cppFileContent);
				
				reactiveClassTransformer.createMsgFiles(rc, modelName, destinationFolder);
			}
			else {
				reactiveClassTransformer.prepare(rc);
				reactiveClassTransformer.transformReactiveClass(rebecaModel, rc, modelName);
				reactiveClassTransformer.createMsgFiles(rc, modelName, destinationFolder);
			}
		}
		
		/* launch file */
		expressionTransformer.prepare(modelName, new ReactiveClassDeclaration(), rebecaModel);
		String launchFileContent = mainTransformer.getLaunchFileContent(rebecaModel, modelName);
		launchDirCreator.createDirectory(destinationFolder, modelName);
		launchDirCreator.addFile(destinationFolder, modelName, modelName + ".launch", launchFileContent);
		
	}
}