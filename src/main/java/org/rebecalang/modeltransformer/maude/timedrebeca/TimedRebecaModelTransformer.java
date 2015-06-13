/*                  In the name of Allah                */
/*                   The best will come                 */

package org.rebecalang.modeltransformer.maude.timedrebeca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.AbstractStatementTransformer;
import org.rebecalang.modeltransformer.TransformingContext;
import org.rebecalang.modeltransformer.TransformingFeature;

/**
 * RMC main class that creates MODERE cpp core classes and its LTL/CTL property
 * C++ files.
 */
public class TimedRebecaModelTransformer extends AbstractModelTransformer {

	public final static String RT_MAUDE_TEMPLATE = "vtl/timedrebeca/RTMaudeTemplate.vm";

	AbstractStatementTransformer statementTransformer;

	@Override
	public void prepare(String modelName, RebecaModel rebecaModel,
			Set<CompilerFeature> compilerFeatures,
			Set<TransformingFeature> transformingFeatures,
			CommandLine commandLine, File destinationLocation,
			ExceptionContainer container) {
		// TODO Auto-generated method stub
		super.prepare(modelName, rebecaModel, compilerFeatures,
				transformingFeatures, commandLine, destinationLocation,
				container);

		Velocity.addProperty("resource.loader", "class");
		// Set vtl loader to the classpath because vtl files are embedded in the result jar file.
		Velocity.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init();

		statementTransformer = new CoreRebecaStatementTransformer(
				new TimedRebecaExpressionTransformer(compilerFeatures,
						transformingFeatures, container), compilerFeatures,
				transformingFeatures);
	}

	@Override
	public void transformModel() throws IOException {
		
		for (ReactiveClassDeclaration rcd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if (rcd.getConstructors().size() != 1) {
				container.addException(doesNotSupportException("reactive classes with more/less than one constructor", 
						rcd.getLineNumber(), rcd.getCharacter()));
				break;
			}
			if (rcd.getSynchMethods().size() > 0) {
				container.addException(doesNotSupportException("synch methods", 
						rcd.getLineNumber(), rcd.getCharacter()));
				break;
			}
		}
			
		VelocityContext context = new VelocityContext();
		context.put("tFeatures", transformingFeatures);
		context.put("cFeatures", compilerFeaturesNames);

		// Creating .h and .cpp files of FTTSTimedModelChecker
		context.put("reactiveClassDeclarations", rebecaModel.getRebecaCode()
				.getReactiveClassDeclaration());
		context.put("mainDefinition", rebecaModel.getRebecaCode()
				.getMainDeclaration());
		context.put("TypesUtilities", TypesUtilities.getInstance());
		context.put("statementTransformer", statementTransformer);
		context.put("TransformingContext", TransformingContext.getInstance());
		context.put("DOUBLE_QUOTE", "\"");

		Template template = Velocity
				.getTemplate(RT_MAUDE_TEMPLATE);
		FileWriter fileWriter = new FileWriter(destinationLocation.getPath()
				+ File.separatorChar
				+ modelName + ".rtmaude");
		template.merge(context, fileWriter);
		fileWriter.close();
	}
}