package org.rebecalang.modeltransformer.ros.timedrebeca;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Annotation;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.SynchMethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSTypesUtilities;
import org.rebecalang.modeltransformer.ros.packageCreator.MsgDirectoryCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/* ROS Node Creator */

@Component
public class ReactiveClassTransformer{
	
	
	public final static String NEW_LINE = "\r\n";
	public final static String TAB = "\t";
	public final static String QUOTE_MARK = "\"";
	public final static String SEMICOLON = ";";
	public final static String publishersQueueSize = "30";
	public final static String subscribersQueueSize = "30";
	
	
//	private ReactiveClassDeclaration rc;
//	private String modelName;
//	private RebecaModel rebecaModel;
	
	@Autowired
	private TimedRebeca2ROSStatementTransformer statementTransformer;
	@Autowired
	private TimedRebeca2ROSExpressionTransformer expressionTransformer;
	
	@Autowired
	MsgDirectoryCreator msgDirectoryCreator;
	
	private Map <Pair<String, String>, String> methodCalls = new HashMap<Pair<String, String>, String>();
	
	private String nodeName;
	
	private String nodePrivateFields;
	private String nodePublishersDefinitions;
	private String nodePublishersCreation;
	private String nodeSubscribersDefinitions;
	private String nodeSubscribersCreation;
	
	
//	public ReactiveClassTransformer(RebecaModel rebecaModel, ReactiveClassDeclaration rc, String modelName, AbstractExpressionTransformer expressionTransformer,
//			Set<CompilerExtension> cFeatures) {
//		this.statementTransformer = new TimedRebeca2ROSStatementTransformer(expressionTransformer, cFeatures);
//		this.expressionTransformer = (TimedRebeca2ROSExpressionTransformer) expressionTransformer;
//		this.rc = rc;
//		this.modelName = modelName;
//		this.rebecaModel = rebecaModel;
//		this.nodeName = rc.getName() + "_node";
//		prepare();
//	}
	
	public void prepare(ReactiveClassDeclaration rcd) {
		/* get all the method calls in order to define publishers later on */
		for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
			statementTransformer.resolveBlockStatement(msgsrv.getBlock());
		}
		statementTransformer.resolveBlockStatement(rcd.getConstructors().get(0).getBlock());
		
		methodCalls = expressionTransformer.getMethodCalls();		
		
	} 
	
	public Map<Pair<String, String>, String> getMethodCalls(){
		return methodCalls;
	}
	
	private String resolveStateVariables(ReactiveClassDeclaration rcd) {
		nodePrivateFields = "";
		for(FieldDeclaration fd : rcd.getStatevars()) {
			for(VariableDeclarator var : fd.getVariableDeclarators()) {
				nodePrivateFields += statementTransformer.resolveVariableDeclaration(fd, var) + ";" + NEW_LINE;
			}
		}
		return nodePrivateFields;
	}
	

	

	private String defineSubscribers(ReactiveClassDeclaration rcd) {
		nodeSubscribersDefinitions = "";
		for(MsgsrvDeclaration msgsrv: rcd.getMsgsrvs()) {
			if(! msgsrv.getAnnotations().isEmpty()) {
				for(Annotation annot: msgsrv.getAnnotations()) {
					if (annot.getIdentifier().equals("Sensor"))
						{
						nodeSubscribersDefinitions += "ros::Subscriber " + msgsrv.getName() + "_sub_sensor" + SEMICOLON + NEW_LINE;
						}
					else
						{
						nodeSubscribersDefinitions += "ros::Subscriber " + msgsrv.getName() + "_sub" + SEMICOLON + NEW_LINE;
						}
				}
			}
			else
			{
			nodeSubscribersDefinitions += "ros::Subscriber " + msgsrv.getName() + "_sub" + SEMICOLON + NEW_LINE;
			}
			
				
		}
		return nodeSubscribersDefinitions;
	}
	
	private String createSubscribers(ReactiveClassDeclaration rcd) {
		nodeSubscribersCreation = "";
		for(MsgsrvDeclaration msgsrv: rcd.getMsgsrvs()) {
			if(! msgsrv.getAnnotations().isEmpty()) {
				for(Annotation annot: msgsrv.getAnnotations()) {
					if (annot.getIdentifier() == "Sensor")
						{
						nodeSubscribersCreation += msgsrv.getName() + "_sub_sensor = " + 
								"n.subscribe(" + QUOTE_MARK + rcd.getName() + "/" + msgsrv.getName() + QUOTE_MARK + ", "
													+ subscribersQueueSize +", &" + rcd.getName() + "::" + 
											msgsrv.getName() + "Callback_Sensor" + ", this)" + SEMICOLON + NEW_LINE;
						}
					else
						{
						nodeSubscribersCreation += msgsrv.getName() + "_sub_sensor = " + 
								"n.subscribe(" + QUOTE_MARK + rcd.getName() + "/" + msgsrv.getName() + QUOTE_MARK + ", "
													+ subscribersQueueSize +", &" + rcd.getName() + "::" + 
											msgsrv.getName() + "Callback" + ", this)" + SEMICOLON + NEW_LINE;
						}
				}
			}
			else
			{
			nodeSubscribersCreation += msgsrv.getName() + "_sub_sensor = " + 
					"n.subscribe(" + QUOTE_MARK + rcd.getName() + "/" + msgsrv.getName() + QUOTE_MARK + ", "
										+ subscribersQueueSize +", &" + rcd.getName() + "::" + 
								msgsrv.getName() + "Callback" + ", this)" + SEMICOLON + NEW_LINE;
			}
			
				
			}
		return nodeSubscribersCreation;
	}
	
	private String definePublishers() {
		nodePublishersDefinitions = "";
		
		if(methodCalls.isEmpty())
			return nodePublishersDefinitions;
		Iterator<Entry<Pair<String, String>, String>> it = methodCalls.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Pair<String, String>, String> entry = 
					(Map.Entry<Pair<String, String>, String>)it.next();
			nodePublishersDefinitions += "ros::Publisher " + entry.getKey().getFirst() + "_" +
					entry.getKey().getSecond() + "_pub" + SEMICOLON + NEW_LINE;
		}
		return nodePublishersDefinitions;	
	}
	
	
	private String createPublishers(ReactiveClassDeclaration rcd, String modelName) {
		nodePublishersCreation = "";

		if(methodCalls.isEmpty())
			return nodePublishersCreation;
		Iterator<Entry<Pair<String, String>, String>> it = methodCalls.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Pair<String, String>, String> entry = 
					(Map.Entry<Pair<String, String>, String>)it.next();
			String topicName = "";
			if(entry.getKey().getFirst().equals("self"))
				topicName += rcd.getName() + "/";
			else
				topicName += entry.getKey().getFirst() + "/";
			topicName += entry.getKey().getSecond();
			nodePublishersCreation += entry.getKey().getFirst() + "_" + entry.getKey().getSecond() + "_pub = " +
					"n.advertise<" + modelName + "::" + entry.getKey().getSecond() +  ">(" + QUOTE_MARK + topicName + QUOTE_MARK + ", " + publishersQueueSize + ")" + SEMICOLON + NEW_LINE;
		}
		
		return nodePublishersCreation;
	}

	private String createNodeMainBody(ReactiveClassDeclaration rcd) {
		String mainContent = "";
	    mainContent += "int main(int argc, char** argv){" + NEW_LINE;
	    mainContent += TAB + "ROS_INFO(\"" + rcd.getName() + " node started\")" + SEMICOLON + NEW_LINE;
	    mainContent += TAB + "ros::init(argc, argv, " + QUOTE_MARK + nodeName + QUOTE_MARK + ")" + SEMICOLON + NEW_LINE;
	    mainContent += TAB + "ros::NodeHandle nh(\"~\");\n";
	    
	    /* store the name of node as "sender" to associate the messages with the name of sender */
	    mainContent += TAB + "std::string sender;" + NEW_LINE;
		mainContent += TAB + " nh.getParam(" + QUOTE_MARK + "sender"+ QUOTE_MARK + ", " + "sender" +");" + NEW_LINE;

	    for(FieldDeclaration knownrebec : rcd.getKnownRebecs()) {
	    	for(VariableDeclarator vd : knownrebec.getVariableDeclarators()) {
	    		mainContent += TAB + " nh.getParam(" + QUOTE_MARK + vd.getVariableName() + QUOTE_MARK + ", " + vd.getVariableName() + ");" + NEW_LINE;
	    	}
	    }
	    /* call the constructor with initial values */
	    String callConstructor = TAB + rcd.getName() + " " + "_" + rcd.getName().toLowerCase();
	    
	    /* if(!rc.getConstructors().get(0).getFormalParameters().isEmpty()) */
	    
	    	callConstructor += "(";
	    	 for(FormalParameterDeclaration param: rcd.getConstructors().get(0).getFormalParameters()) {
	    		 String type = "std::string";
	    		 if(param.getType() == CoreRebecaTypeSystem.INT_TYPE)
	    			 type = "int";
	    		 if(param.getType() == CoreRebecaTypeSystem.DOUBLE_TYPE)
	    			 type = "double";
	    		 if(param.getType() == CoreRebecaTypeSystem.BOOLEAN_TYPE)
	    			 type = "bool";
	    		 mainContent += TAB + type + " " + param.getName() + SEMICOLON + NEW_LINE;
	    		mainContent += TAB + " nh.getParam(" + QUOTE_MARK + param.getName()+ QUOTE_MARK + ", " + param.getName() +");" + NEW_LINE;
	    		callConstructor += param.getName() + ", ";
	    	}
	    	/* callConstructor = callConstructor.substring(0, callConstructor.length() - 2); */
	    	callConstructor += "sender";
	    	callConstructor += ")";

	    
	    mainContent += callConstructor;
	    mainContent += SEMICOLON + NEW_LINE;
	    mainContent += "}" + NEW_LINE;
	    return mainContent;
	}
	
	private String createNodeConstructorSignature(ReactiveClassDeclaration rcd) {
		String retValue = "";
		retValue += rcd.getConstructors().get(0).getName();
		retValue += "(";
		/* if(rc.getConstructors().get(0).getFormalParameters().isEmpty()) {
			retValue += ")";
			return retValue;
		} */
		for(FormalParameterDeclaration arg : rcd.getConstructors().get(0).getFormalParameters()) {
			retValue +=  Rebeca2ROSTypesUtilities.getCppType(arg.getType().getTypeName()) + " " + arg.getName() + ", ";
		}
		retValue += "std::string _sender";
		/* retValue = retValue.substring(0, retValue.length() - 2); */
		retValue += ")";
		return retValue;
	}
	
	private String createNodeConstructorBody(ReactiveClassDeclaration rcd, String modelName) {
		String retValue = "";
		retValue += createSubscribers(rcd) + createPublishers(rcd, modelName);
		retValue += "sender = _sender" + SEMICOLON + NEW_LINE;
		if(! methodCalls.isEmpty()) {
			retValue += "while(";
			Iterator<Entry<Pair<String, String>, String>> it = methodCalls.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Pair<String, String>, String> entry = 
						(Map.Entry<Pair<String, String>, String>)it.next();
				retValue += entry.getKey().getFirst() + "_" +
						entry.getKey().getSecond() + "_pub" + ".getNumSubscribers() < 1 ||";
			}
			retValue = retValue.substring(0, retValue.length() - 2);
			retValue += ")" + SEMICOLON + NEW_LINE;
		}
		retValue += statementTransformer.resolveBlockStatement(rcd.getConstructors().get(0).getBlock());
		retValue += "ros::spin()" + SEMICOLON + NEW_LINE; /*start processing of call back functions */
		return retValue;
	}
	
	private String getIncludes(RebecaModel rebecaModel, String modelName) {
		String includes = "";
		includes += "#include <ros/ros.h>" + NEW_LINE;
		for(ReactiveClassDeclaration rcd: rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			for(MsgsrvDeclaration msgsrv: rcd.getMsgsrvs()) {
				includes += "#include <" + modelName + File.separatorChar + 
							msgsrv.getName() + ".h" + ">" + NEW_LINE;
			}
		}
		includes += "#include <string>" + NEW_LINE;
		includes += "#include <stdlib.h> // sleep function\n";
		includes += "#include <bitset>" + NEW_LINE;
		
		includes += "#include <geometry_msgs/Twist.h> // for ROS movement commands \n";

		includes += "typedef std::bitset<8> byte;\n";
		return includes;
	}

	public String getHeaderFileContent(RebecaModel rebecaModel, ReactiveClassDeclaration rcd, String modelName) {
		String headerFileContent = "";
		headerFileContent += getIncludes(rebecaModel, modelName) + NEW_LINE;
		headerFileContent += "class" + " " + rcd.getName() + "{" + NEW_LINE;
		headerFileContent += "public:" + NEW_LINE;
		headerFileContent += createNodeConstructorSignature(rcd) + SEMICOLON + NEW_LINE;
		
		for (MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
			if(! msgsrv.getAnnotations().isEmpty()) {
				for(Annotation annot: msgsrv.getAnnotations()) {
					if (annot.getIdentifier().equals("Sensor"))
						{
						SensorTransformer sensorTransformer =
								new SensorTransformer(msgsrv, modelName);
						headerFileContent += "void " + sensorTransformer.getCallbackFunctionSignature() + SEMICOLON + NEW_LINE;
						}
					else
						{
						MessageServerTransformer messageServerTransformer =
								new MessageServerTransformer(statementTransformer, msgsrv, modelName);
						headerFileContent += "void " + messageServerTransformer.getCallbackFunctionSignature() + SEMICOLON + NEW_LINE;
						}
				}
			}
			else
			{
			MessageServerTransformer messageServerTransformer =
					new MessageServerTransformer(statementTransformer, msgsrv, modelName);
			headerFileContent += "void " + messageServerTransformer.getCallbackFunctionSignature() + SEMICOLON + NEW_LINE;
			}
			
			
		}
		
		headerFileContent += "private:" + NEW_LINE;
		
		for(SynchMethodDeclaration method : rcd.getSynchMethods()) {
			headerFileContent += Rebeca2ROSTypesUtilities.getCppType(method.getReturnType().getTypeName());
			headerFileContent += " ";
			headerFileContent += method.getName() + "(";
			for(FormalParameterDeclaration param : method.getFormalParameters()) {
				headerFileContent += Rebeca2ROSTypesUtilities.getCorrespondingCppType(param.getType());
				headerFileContent += " " + param.getName() + ",";
			}
			if(! method.getFormalParameters().isEmpty())
				headerFileContent = headerFileContent.substring(0, headerFileContent.length() - 1);
			headerFileContent += ")" + SEMICOLON + NEW_LINE;
			
		}
	
		headerFileContent += "/*ROS Fields*/" + NEW_LINE;
		headerFileContent += "ros::NodeHandle n" + SEMICOLON + NEW_LINE;
		headerFileContent += definePublishers();
		headerFileContent += defineSubscribers(rcd);
		
		headerFileContent += "/* Reactive Class State Variables as Private Fields */" + NEW_LINE;
		headerFileContent += resolveStateVariables(rcd);
		headerFileContent += "std::string sender" + SEMICOLON + NEW_LINE;
		
		/* for(FieldDeclaration vd : rc.getKnownRebecs()) {
			for(VariableDeclarator knownrebec : vd.getVariableDeclarators()) {
			headerFileContent += "std::string " + knownrebec.getVariableName() + " = " +
								QUOTE_MARK + knownrebec.getVariableName() + QUOTE_MARK + ";" + NEW_LINE;
			}
		} */
		
		headerFileContent += resolveEnvironmentVariables(rebecaModel);
		headerFileContent += "}" + SEMICOLON;
		return headerFileContent;
	}


	private String resolveEnvironmentVariables(RebecaModel rebecaModel) {
		String retValue = "";
		for(FieldDeclaration fd : rebecaModel.getRebecaCode().getEnvironmentVariables()) {
			retValue += "const ";
			for(VariableDeclarator vd : fd.getVariableDeclarators()) {
				retValue += statementTransformer.resolveVariableDeclaration(fd, vd) + ";" + NEW_LINE;
			}
		}
		return retValue;
	}

	public String getCppFileContent(ReactiveClassDeclaration rcd, String modelName) {
		String retValue = "";
		
		retValue += "#include <" + modelName + File.separatorChar
				 + rcd.getName() + ".h" + ">" + NEW_LINE + NEW_LINE;
		
		retValue += NEW_LINE + "/* the following variables are needed for using sender keyword */" + NEW_LINE;
		for(FieldDeclaration knwonrebec: rcd.getKnownRebecs()) {
			for(VariableDeclarator vd : knwonrebec.getVariableDeclarators()) {
				retValue += "std::string " + vd.getVariableName() + SEMICOLON + NEW_LINE;
			}
		}
		
		retValue += createNodeMainBody(rcd) + NEW_LINE + NEW_LINE;
		
		retValue += rcd.getName() + "::" + createNodeConstructorSignature(rcd) + "{" + NEW_LINE
				 +createNodeConstructorBody(rcd, modelName) + "}" + NEW_LINE + NEW_LINE;
		
		for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
			if(! msgsrv.getAnnotations().isEmpty()) {
				for(Annotation annot: msgsrv.getAnnotations()) {
					if (annot.getIdentifier() == "Sensor")
						{
						SensorTransformer sensorTransformer = new SensorTransformer(msgsrv, modelName);
						retValue += "void " + rcd.getName() + "::" + sensorTransformer.getCallbackFunctionSignature() + 
								"{" + NEW_LINE + sensorTransformer.getCallbackFunctionBody() + "}" + NEW_LINE + NEW_LINE;
						}
					else
						{
						MessageServerTransformer messageServerTransformer = new MessageServerTransformer(statementTransformer, msgsrv, modelName);
						retValue += "void " + rcd.getName() + "::" + messageServerTransformer.getCallbackFunctionSignature() + 
								"{" + NEW_LINE + messageServerTransformer.getCallbackFunctionBody() + "}" + NEW_LINE + NEW_LINE;
						}
				}
			}
			else
			{
			MessageServerTransformer messageServerTransformer = new MessageServerTransformer(statementTransformer, msgsrv, modelName);
			retValue += "void " + rcd.getName() + "::" + messageServerTransformer.getCallbackFunctionSignature() + 
					"{" + NEW_LINE + messageServerTransformer.getCallbackFunctionBody() + "}" + NEW_LINE + NEW_LINE;
			}
			
			
		}
		
		for(SynchMethodDeclaration method : rcd.getSynchMethods()) {
			retValue += transformSynchMethod(rcd, method);
		} 
		return retValue;
	}

	private String transformSynchMethod(ReactiveClassDeclaration rcd, SynchMethodDeclaration method) {
		String retValue = "";
		retValue += Rebeca2ROSTypesUtilities.getCppType(method.getReturnType().getTypeName());
		retValue += " " + rcd.getName() + "::" + method.getName() + "(";
		for(FormalParameterDeclaration param : method.getFormalParameters()) {
			retValue += Rebeca2ROSTypesUtilities.getCorrespondingCppType(param.getType());
			retValue += " " + param.getName() + ",";
		}
		if(! method.getFormalParameters().isEmpty())
			retValue = retValue.substring(0, retValue.length() - 1);
		retValue += ")";
		retValue += "{" + NEW_LINE;
		retValue += statementTransformer.resolveBlockStatement(method.getBlock());
		retValue += "}" + NEW_LINE + NEW_LINE;
		return retValue;
	}

	public void createMsgFiles(ReactiveClassDeclaration rcd, String modelName, File destinationLocation) throws IOException {
		msgDirectoryCreator.createDirectory(destinationLocation, modelName);
		for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
			MessageServerTransformer messageServerTransformer = new MessageServerTransformer(statementTransformer, msgsrv, modelName);
			msgDirectoryCreator.addFile(destinationLocation, modelName, msgsrv.getName() + ".msg", messageServerTransformer.getMsgFileContent());
		}
	}


	public void transformReactiveClass(RebecaModel rebecaModel, ReactiveClassDeclaration rcd, String modelName) {
		if(! rcd.getAnnotations().isEmpty()) {
			for(Annotation annot: rcd.getAnnotations()) {
				if (annot.getIdentifier() == "Robot")
					transformTopicClass();
				if (annot.getIdentifier() == "Controller")
					transformTopicClass();
			}
			
		}
	}
	
	private void transformTopicClass() {
	}

}