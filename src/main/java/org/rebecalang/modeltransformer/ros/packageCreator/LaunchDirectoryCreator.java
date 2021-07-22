package org.rebecalang.modeltransformer.ros.packageCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LaunchDirectoryCreator{
	
	@Autowired
	ExceptionContainer exceptionContainer;
	
	private String getPath(File destinationLocation, String modelName) {
		String rosPackagePath = destinationLocation.getAbsolutePath() + File.separatorChar + modelName;
		return rosPackagePath + File.separatorChar + "launch";
	}

	public boolean createDirectory(File destinationLocation, String modelName) {
		boolean success = true;
		File file = new File(getPath(destinationLocation, modelName));
		file.mkdirs();
		return success;
	}
	
	public boolean addFile(File destinationLocation, String modelName, String fileName, String fileContent) throws IOException {
		boolean success = true;
		
		/* create file */
		String filePath = getPath(destinationLocation, modelName) + File.separatorChar + fileName;
		File file = new File(filePath);
		try{
			file.createNewFile();
		} catch(IOException e) {
			exceptionContainer.addException(e);
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
	    	 	exceptionContainer.addException(e);
				e.printStackTrace();
				success = false;
			}	
		return success;
	}
	
	
	
}


