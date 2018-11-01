package org.rebecalang.modeltransformer.ros.timedrebeca;


import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class ConfigFilesCreator {
	private RebecaModel rebecaModel;
	private String projectName;
	private final static String NEW_LINE = "\r\n";
	public final static String QUOTE_MARK = "\"";
	public final static String SEMICOLON = ";";
	private String cmakeListversion = "2.8.3";
	private String xmlVersion = "1.0";
	public ConfigFilesCreator(String modelName, RebecaModel rebecaModel) {
		this.projectName = modelName;
		this.rebecaModel = rebecaModel;
		
	}
	
	public String getPackageXmlFileContent() {
		String retValue = "";
		retValue += "<?xml version=" + QUOTE_MARK + xmlVersion + QUOTE_MARK + "?>" + NEW_LINE;
		retValue += "<package format=" + QUOTE_MARK + 2 + QUOTE_MARK + ">" + NEW_LINE;
		retValue += "<name>" + projectName + "</name>" + NEW_LINE;
		retValue += "<version>0.0.0</version>" + NEW_LINE;
		retValue += "<description>The" + projectName +  "package</description>" + NEW_LINE;
		retValue += "<maintainer email=" + QUOTE_MARK +
				"salmani.bahar@gmail.com" + QUOTE_MARK + ">" + "Bahar" + "</maintainer>" + NEW_LINE;
		retValue += "<license>TODO</license>" + NEW_LINE;

		retValue += "  <buildtool_depend>catkin</buildtool_depend>\n" + 
				"  <build_depend>roscpp</build_depend>\n" + 
				"  <build_depend>rospy</build_depend>\n" + 
				"  <build_depend>std_msgs</build_depend>\n" + 
				"  <build_depend>message_generation</build_depend>\n" + 
				"  <build_export_depend>roscpp</build_export_depend>\n" + 
				"  <build_export_depend>rospy</build_export_depend>\n" + 
				"  <build_export_depend>std_msgs</build_export_depend>\n" + 
				"  <exec_depend>message_runtime</exec_depend>\n" + 
				"  <exec_depend>roscpp</exec_depend>\n" + 
				"  <exec_depend>rospy</exec_depend>\n" + 
				"  <exec_depend>std_msgs</exec_depend>\n";
		retValue += "</package>";
		return retValue;
	}
	
	private String addExecutables() {
		String retValue = "";
		for(ReactiveClassDeclaration rc : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if(! rc.getAnnotations().isEmpty())
				break;
			retValue += "add_executable(" + rc.getName() + "_node" + 
					" " + "src/" + rc.getName() + ".cpp" + ")" + NEW_LINE;
			retValue += "add_dependencies(" + rc.getName() + "_node " + projectName + "_generate_messages_cpp)" + NEW_LINE;
			retValue += "target_link_libraries(" + rc.getName() + "_node" +
					" " + "${catkin_LIBRARIES}" + ")" + NEW_LINE;
		}
		return retValue;
	}

	private String addMessageFiles() {
		String retValue = "";
		retValue += "add_message_files(" + NEW_LINE + "FILES" + NEW_LINE;
		for(ReactiveClassDeclaration rc : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			for(MsgsrvDeclaration msgsrv: rc.getMsgsrvs()) {
				retValue += msgsrv.getName() + ".msg" + NEW_LINE;
			}
		}
		retValue += ")" + NEW_LINE + NEW_LINE;
		return retValue;
	}

	public String getCmakeListFileContent() {
		String retValue = "";
		retValue += "cmake_minimum_required(VERSION " + cmakeListversion + ")" + NEW_LINE;
		retValue += "project(" + projectName + ")" + NEW_LINE;
		retValue += "find_package(catkin REQUIRED COMPONENTS\n" + 
				"  roscpp\n" + 
				"  rospy\n" + 
				"  std_msgs\n" + 
				"  message_generation\n" + 
			   "${MESSAGE_DEPENDENCIES}" +
				")" + NEW_LINE + NEW_LINE;
		
		retValue += addMessageFiles();
		
		retValue += "generate_messages(" + NEW_LINE + "DEPENDENCIES" + NEW_LINE
				+ "std_msgs" + NEW_LINE + ")" + NEW_LINE + NEW_LINE;
		
		retValue += "catkin_package(" + NEW_LINE +
				"CATKIN_DEPENDS roscpp rospy std_msgs message_runtime" + NEW_LINE +
				")" + NEW_LINE + NEW_LINE;
		
		retValue += "include_directories(" + NEW_LINE
				 + "include" + NEW_LINE
				 + "${catkin_INCLUDE_DIRS}" + NEW_LINE
				 + ")" + NEW_LINE + NEW_LINE;
		
		retValue += addExecutables();

		return retValue;
	}
	
	

}
