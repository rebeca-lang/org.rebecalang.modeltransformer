package org.rebecalang.modeltransformer.ros.packageCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.rebecalang.compiler.utils.ExceptionContainer;

public class MsgDirectoryCreator{
	
	protected String dirPath;
	protected String rosPackagePath;
	protected ExceptionContainer container;
	public MsgDirectoryCreator(File destinationLocation, String modelName, ExceptionContainer container) {				
		this.rosPackagePath = destinationLocation.getAbsolutePath() + File.separatorChar + modelName;
		this.dirPath = rosPackagePath + File.separatorChar + "msg";
		this.container = container;
		this.createDirectory();
	}

	public boolean createDirectory() {
		boolean success = true;
		File file = new File(dirPath);
		file.mkdirs();
		return success;
	}
	
	public boolean addFile(String fileName, String fileContent) throws IOException {
		boolean success = true;
		
		/* create file */
		String filePath = dirPath + File.separatorChar + fileName;
		File file = new File(filePath);
		try{
			file.createNewFile();
		} catch(IOException e) {
			container.addException(e);
			e.printStackTrace();
			success = false;
		}
		
		/* fill the file */
	     try {
	         Writer writer = new FileWriter(file);
	         BufferedWriter bufferedWriter = new BufferedWriter(writer);
	         bufferedWriter.write(fileContent);
	         bufferedWriter.close();
	     } catch (IOException e) {
	    	 	container.addException(e);
				e.printStackTrace();
				success = false;
			}	
		return success;
	}
	
	
	
}


