package org.rebecalang.modeltransformer.ros;

import java.io.File;

import org.apache.commons.cli.CommandLine;

public class Rebeca2ROSProperties {
	String modelName;
	File destinationFolder;
	
	public Rebeca2ROSProperties() {
		
	}
	
	public Rebeca2ROSProperties(CommandLine commandLine) {
		
	}
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public File getDestinationFolder() {
		return destinationFolder;
	}
	public void setDestinationFolder(File destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
	
	
}
