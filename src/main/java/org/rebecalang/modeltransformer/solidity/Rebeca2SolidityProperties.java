package org.rebecalang.modeltransformer.solidity;

import java.io.File;

import org.apache.commons.cli.CommandLine;

public class Rebeca2SolidityProperties {
	String modelName;
	File destinationFolder;
	
	public Rebeca2SolidityProperties() {
		
	}
	
	public Rebeca2SolidityProperties(CommandLine commandLine) {
		
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
