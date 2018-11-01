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
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modeltransformer.akka.Rebeca2AKKATransformer;
import org.rebecalang.modeltransformer.maude.Rebeca2MaudeModelTransformer;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSModelTransformer;

public class Transform {
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
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

			if (commandLine.hasOption("help"))
				throw new ParseException("");
			// Set Rebeca file reference.
			File rebecaFile = new File(commandLine.getOptionValue("source"));

			// Set output location. Default location is rmc-output folder.
			File destination;
			if (commandLine.hasOption("output")) {
				destination = new File(commandLine.getOptionValue("output"));
			} else {
				destination = new File("output-dir");
			}

			Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
			CompilerFeature coreVersion = null;
			if (commandLine.hasOption("version")) {
				String version = commandLine.getOptionValue("version");
				if (version.equals("2.0"))
					coreVersion = CompilerFeature.CORE_2_0;
				else if (version.equals("2.1"))
					coreVersion = CompilerFeature.CORE_2_1;
				else if (version.equals("2.2"))
					coreVersion = CompilerFeature.CORE_2_2;
				else {
					throw new ParseException("Unrecognized Rebeca version: "
							+ version);
				}
			} else {
				coreVersion = CompilerFeature.CORE_2_0;
			}
			compilerFeatures.add(coreVersion);

			String extensionLabel;
			if (commandLine.hasOption("extension")) {
				extensionLabel = commandLine.getOptionValue("extension");
			} else {
				extensionLabel = "CoreRebeca";
			}
			if (extensionLabel.equals("CoreRebeca")) {

			} else if (extensionLabel.equals("TimedRebeca")) {
				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			} else if (extensionLabel.equals("ProbabilisticRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
			} else if (extensionLabel.equals("ProbabilisticTimedRebeca")) {
				compilerFeatures.add(CompilerFeature.PROBABILISTIC_REBECA);
				compilerFeatures.add(CompilerFeature.TIMED_REBECA);
			} else {
				throw new ParseException("Unrecognized Rebeca extension: "
						+ extensionLabel);
			}

			Set<TransformingFeature> analysisFeatures = new HashSet<TransformingFeature>();
			ExceptionContainer container = new ExceptionContainer();

			if (commandLine.hasOption("target")) {
				String target = commandLine.getOptionValue("target");
				if (target.equalsIgnoreCase("RTMaude")) {
					Rebeca2MaudeModelTransformer.getInstance().transformModel(rebecaFile,
							destination, compilerFeatures, analysisFeatures,
							commandLine);
					container = Rebeca2MaudeModelTransformer.getInstance()
							.getExceptionContainer();
				}else if (target.equalsIgnoreCase("ROS")) {
					if (compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
						System.out.println("Rebeca to ROS transformer only works for core Rebeca and Timed Rebeca");
					}
					Rebeca2ROSModelTransformer.getInstance().transformModel(rebecaFile, destination, compilerFeatures, analysisFeatures, commandLine);
					container = Rebeca2ROSModelTransformer.getInstance().getExceptionContainer();
				} else if (target.equalsIgnoreCase("akka")) {
					if (compilerFeatures.contains(CompilerFeature.TIMED_REBECA) ||
							compilerFeatures.contains(CompilerFeature.PROBABILISTIC_REBECA)) {
						System.out.println("Rebeca to Akka transformer only works for Core Rebeca models.");
						return;
					}
					if (compilerFeatures.contains(CompilerFeature.CORE_2_0)) {
						System.out.println("Rebeca to Akka transformer works for Rebeca core 2.1 or upper.");
						return;						
					}
					Rebeca2AKKATransformer.getInstance().transformModel(rebecaFile,
							destination, compilerFeatures, analysisFeatures,
							commandLine);
					container = Rebeca2MaudeModelTransformer.getInstance()
							.getExceptionContainer();
				} else {
					throw new ParseException("Unrecognized target \""
							+ target +"\".");
				}
			}


			for (Exception e : container.getWarnings()) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					System.out.println("Line " + ce.getLine() + ", Warning: "
							+ ce.getMessage());
				} else {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

			for (Exception e : container.getExceptions()) {
				if (e instanceof CodeCompilationException) {
					CodeCompilationException ce = (CodeCompilationException) e;
					System.out.println("Line " + ce.getLine() + ", Error: "
							+ ce.getMessage());
				} else {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

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
