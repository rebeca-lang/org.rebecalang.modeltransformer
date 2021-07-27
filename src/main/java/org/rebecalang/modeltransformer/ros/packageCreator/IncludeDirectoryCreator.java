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
public class IncludeDirectoryCreator{
	
	@Autowired
	protected ExceptionContainer exceptionContainer;

//	public IncludeDirectoryCreator(File destinationLocation, String modelName) {
//		String rosPackagePath = destinationLocation.getAbsolutePath() + File.separatorChar + modelName;
//		this.dirPath = rosPackagePath + File.separatorChar + "include" + File.separatorChar + modelName;
//		this.createDirectory();
//	}

	private String getPath(File destinationFolder, String modelName) {
		String rosPackagePath = destinationFolder.getAbsolutePath() + File.separatorChar + modelName;
		return rosPackagePath + File.separatorChar + "include" + File.separatorChar + modelName;
	}

	public boolean createDirectory(File destinationFolder, String modelName) {
		boolean success = true;
		File file = new File(getPath(destinationFolder, modelName));
		file.mkdirs();
		return success;
	}
	
	public boolean addFile(File destinationFolder, String modelName, String fileName, String fileContent) throws IOException {
		boolean success = true;
		
		/* create file */
		String filePath = getPath(destinationFolder, modelName) + File.separatorChar + fileName;
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


