package org.rebecalang.modeltransformer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSModelTransformer;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSProperties;
import org.rebecalang.modeltransformer.solidity.Rebeca2SolidityModelTransformer;
import org.rebecalang.modeltransformer.solidity.Rebeca2SolidityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@SuppressWarnings("deprecation")
@Component
public class Transform {

	@Autowired
	RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	ExceptionContainer exceptionContainer;
	
	@Autowired
	Rebeca2SolidityModelTransformer Rebeca2SolidityModelTransformer;
	
	@Autowired
	Rebeca2RILModelTransformer rebeca2RILModelTransformer;
	
	@Autowired
	Rebeca2ROSModelTransformer rebeca2ROSModelTransformer;
	
	@SuppressWarnings({ "static-access", "resource"})
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(CompilerConfig.class, ModelTransformerConfig.class);
        Transform transform = context.getBean(Transform.class);
		
		CommandLineParser cmdLineParser = new GnuParser();
		Options options = new Options();
		try {
			Option option = OptionBuilder
					.withArgName("file")
					.hasArg()
					.withDescription(
							"Generated C++ source files location. Default location is \'./rmc-output\' folder.")
					.withLongOpt("output").create('o');
			options.addOption(option);

			option = OptionBuilder.withArgName("file").hasArg()
					.withDescription("Rebeca model source file.")
					.withLongOpt("source").create('s');
			option.setRequired(true);
			options.addOption(option);

			option = OptionBuilder
					.withArgName("value")
					.hasArg()
					.withDescription(
							"Rebeca compiler version (2.0, 2.1, or 2.2). Default version is 2.0")
					.withLongOpt("version").create('v');
			options.addOption(option);

			option = OptionBuilder
					.withArgName("value")
					.hasArg()
					.withDescription(
							"Rebeca model extension (CoreRebeca/TimedRebeca/ProbabilisticRebeca/"
									+ "ProbabilisticTimedRebeca). Default is \'CoreRebeca\'.")
					.withLongOpt("extension").create('e');
			options.addOption(option);

			option = OptionBuilder
					.withArgName("value")
					.hasArg()
					.withDescription(
							"Target model (Only RTMaude is valid in this version).")
					.withLongOpt("target").create('t');
			option.setRequired(true);
			options.addOption(option);

			option = OptionBuilder
					.withArgName("output-dir")
					.hasOptionalArg()
					.withDescription(
							"Export transformed model in the \"output-dir\" directory.")
					.withLongOpt("output").create("o");
			options.addOption(option);

			options.addOption(new Option("h", "help", false,
					"Print this message."));

			CommandLine commandLine = cmdLineParser.parse(options, args);

			if (commandLine.hasOption("help")) {
				throw new ParseException("");
			}
			// Set Rebeca file reference.
			File rebecaFile = new File(commandLine.getOptionValue("source"));

			CoreVersion coreVersion = null;
			if (commandLine.hasOption("version")) {
				String version = commandLine.getOptionValue("version");
				if (version.equals("2.0"))
					coreVersion = CoreVersion.CORE_2_0;
				else if (version.equals("2.1"))
					coreVersion = CoreVersion.CORE_2_1;
				else if (version.equals("2.2"))
					coreVersion = CoreVersion.CORE_2_2;
				else if (version.equals("2.3"))
					coreVersion = CoreVersion.CORE_2_3;
				else {
					throw new ParseException("Unrecognized Rebeca version: "
							+ version);
				}
			} else {
				coreVersion = CoreVersion.CORE_2_0;
			}

			Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
			String extensionLabel;
			if (commandLine.hasOption("extension")) {
				extensionLabel = commandLine.getOptionValue("extension");
				if (extensionLabel.equals("CoreRebeca")) {

				} else if (extensionLabel.equals("TimedRebeca")) {
					extension.add(CompilerExtension.TIMED_REBECA);
				} else if (extensionLabel.equals("ProbabilisticRebeca")) {
					extension.add(CompilerExtension.PROBABILISTIC_REBECA);
				} else if (extensionLabel.equals("ProbabilisticTimedRebeca")) {
					extension.add(CompilerExtension.PROBABILISTIC_REBECA);
					extension.add(CompilerExtension.TIMED_REBECA);
				} else {
					throw new ParseException("Unrecognized Rebeca extension: "
							+ extensionLabel);
				}
			} else {
				extensionLabel = "CoreRebeca";
			}


			Pair<RebecaModel, SymbolTable> compilationResult = 
					transform.rebecaModelCompiler.compileRebecaFile(
							rebecaFile, extension, coreVersion);
			
			if (!transform.exceptionContainer.exceptionsIsEmpty()) {
				return;
			}
			
			if (commandLine.hasOption("target")) {
				String target = commandLine.getOptionValue("target");
				if (target.equalsIgnoreCase("ROS")) {
					if (!extension.contains(CompilerExtension.TIMED_REBECA) || extension.size() != 1) {
						System.out.println("Rebeca to ROS transformer only works for core Rebeca and Timed Rebeca");
					}
					Rebeca2ROSProperties properties = new Rebeca2ROSProperties();
					properties.setDestinationFolder(null);
					properties.setModelName(null);
					transform.rebeca2ROSModelTransformer.transformModel(compilationResult, extension, coreVersion, properties);
				} else if (target.equalsIgnoreCase("RIL")) {
					if (extension.contains(CompilerExtension.TIMED_REBECA) ||
							extension.contains(CompilerExtension.PROBABILISTIC_REBECA)) {
						System.out.println("Rebeca to RIL transformer only works for Core Rebeca models (for now).");
						return;
					}
					if (coreVersion == CoreVersion.CORE_2_0) {
						System.out.println("Rebeca to RIL transformer works for Rebeca core 2.1 or upper.");
						return;						
					}
				} else if (target.equalsIgnoreCase("SOLIDITY")) {
					if (extension.contains(CompilerExtension.TIMED_REBECA) ||
							extension.contains(CompilerExtension.PROBABILISTIC_REBECA)) {
						System.out.println("Rebeca to Solidity transformer only works for Core Rebeca models (for now).");
						return;
					}
					if (coreVersion == CoreVersion.CORE_2_0) {
						System.out.println("Rebeca to Solidity transformer works for Rebeca core 2.1 or upper.");
						return;						
					}
					
					Rebeca2SolidityProperties properties = new Rebeca2SolidityProperties();
					properties.setDestinationFolder(null);
					properties.setModelName(null);
					transform.Rebeca2SolidityModelTransformer.transformModel(compilationResult, extension, coreVersion, properties);
					//TODO remove it when all the parts are transformed to Spring DI
				} else {
					throw new ParseException("Unrecognized target \""
							+ target +"\".");
				}
			}

			System.out.println(transform.exceptionContainer);

		} catch (ParseException e) {
			if (!e.getMessage().isEmpty())
				System.out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("transformer [options]", options);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unexpected exception: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("transformer [options]", options);
		}
	}
}
